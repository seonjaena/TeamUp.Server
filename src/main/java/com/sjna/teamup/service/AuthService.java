package com.sjna.teamup.service;

import com.sjna.teamup.dto.JwtDto;
import com.sjna.teamup.dto.request.LoginRequest;
import com.sjna.teamup.dto.request.EmailVerificationCodeRequest;
import com.sjna.teamup.dto.request.PhoneVerificationCodeRequest;
import com.sjna.teamup.dto.response.LoginResponse;
import com.sjna.teamup.dto.response.RefreshAccessTokenResponse;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRefreshToken;
import com.sjna.teamup.entity.enums.VERIFICATION_CODE_TYPE;
import com.sjna.teamup.exception.*;
import com.sjna.teamup.exception.AlreadyUserPhoneExistsException;
import com.sjna.teamup.repository.UserRefreshTokenRepository;
import com.sjna.teamup.security.JwtProvider;
import com.sjna.teamup.sender.EmailSender;
import com.sjna.teamup.sender.SMSSender;
import com.sjna.teamup.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    @Value("${service.email.verification.valid-minute:10}")
    private Integer emailVerificationValidMinute;

    @Value("${service.phone.verification.valid-minute:10}")
    private Integer phoneVerificationValidMinute;

    @PersistenceContext
    private EntityManager em;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailSender emailSender;
    private final SMSSender smsSender;
    private final MessageSource messageSource;

    @Transactional
    public LoginResponse login(LoginRequest loginRequestDto) throws NoSuchAlgorithmException {

        User dbUser = userService.getUser(loginRequestDto.getUserId());

        if(!passwordEncoder.matches(loginRequestDto.getUserPw(), dbUser.getAccountPw())) {
            log.warn("Password is incorrect. userId={}", loginRequestDto.getUserId());
            throw new UnAuthenticatedException(
                    messageSource.getMessage("error.user-id-pw.incorrect",
                            new String[] {},
                            LocaleContextHolder.getLocale())
            );
        }

        // JWT Token 생성(Refresh, Access)
        JwtDto jwt = jwtProvider.createToken(dbUser.getAccountId(), List.of(dbUser.getRole().getName()));
        // refresh token의 인덱스를 생성한다. (클라이언트에 전달)
        String refreshTokenIdxHash = StringUtil.getMd5(System.currentTimeMillis() + dbUser.getAccountId());

        // 이미 refresh token이 있다면 삭제하고 저장, 없다면 그냥 저장
        Optional<UserRefreshToken> savedRefreshToken = userRefreshTokenRepository.findByUser(dbUser);
        if(savedRefreshToken.isPresent()) {
            userRefreshTokenRepository.delete(savedRefreshToken.get());
            em.flush();
        }
        userRefreshTokenRepository.save(UserRefreshToken.from(refreshTokenIdxHash, dbUser, jwt.getRefreshToken()));

        return new LoginResponse(jwt.getAccessToken(), refreshTokenIdxHash);
    }

    @Transactional
    public RefreshAccessTokenResponse refreshAccessToken(String refreshTokenIdxHash) throws NoSuchAlgorithmException {
        UserRefreshToken refreshToken = userRefreshTokenRepository.findByIdxHash(refreshTokenIdxHash)
                .orElseThrow(() -> new UnAuthenticatedException(
                        messageSource.getMessage("notice.re-login.request",
                                new String[] {},
                                LocaleContextHolder.getLocale())
                ));

        String userId = jwtProvider.getUserId(refreshToken.getValue());
        List<String> userRoles = jwtProvider.getUserRoles(refreshToken.getValue());

        // hash만 바꿔서 DB Update 참고: https://brunch.co.kr/@anonymdevoo/37
        String newRefreshTokenIdxHash = StringUtil.getMd5(System.currentTimeMillis() + userId);
        refreshToken.changeIdxHash(newRefreshTokenIdxHash);
        userRefreshTokenRepository.save(refreshToken);

        // Access Token 재발급
        return new RefreshAccessTokenResponse(jwtProvider.refreshAccessToken(userId, userRoles), newRefreshTokenIdxHash);
    }

    public void sendVerificationCode(EmailVerificationCodeRequest verificationCodeRequest) {
        Locale locale = LocaleContextHolder.getLocale();
        String verificationCode = createVerificationCode(VERIFICATION_CODE_TYPE.EMAIL);

        // 만약 이미 회원가입된 사용자 중 동일한 이메일이 존재한다면 실패로 처리
        if(!userService.checkUserIdAvailable(verificationCodeRequest.getEmail())) {
            throw new AlreadyUserEmailExistsException(messageSource.getMessage("error.email.already-exist", null, locale));
        }

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    operations.delete("verificationCode_" + verificationCodeRequest.getEmail());
                    operations.opsForValue().set("verificationCode_" + verificationCodeRequest.getEmail(), verificationCode, emailVerificationValidMinute, TimeUnit.MINUTES);

                    String emailSubject = messageSource.getMessage("email.verification.subject", null, locale);
                    String emailBody = messageSource.getMessage("email.verification.body", new String[]{verificationCode, String.valueOf(emailVerificationValidMinute)}, locale);
                    emailSender.sendRawEmail(List.of(verificationCodeRequest.getEmail()), emailSubject, emailBody);

                    return operations.exec();
                }catch(SendEmailFailureException e) {
                    log.error("Failed to send verification code", e);
                    operations.discard();
                    throw e;
                }
            }
        });

    }

    public void sendVerificationCode(PhoneVerificationCodeRequest verificationCodeRequest) {
        Locale locale = LocaleContextHolder.getLocale();
        String verificationCode = createVerificationCode(VERIFICATION_CODE_TYPE.PHONE);

        // 만약 이미 회원가입된 사용자 중 동일한 이메일이 존재한다면 실패로 처리
        if(!userService.checkUserPhoneAvailable(verificationCodeRequest.getPhone())) {
            throw new AlreadyUserPhoneExistsException(messageSource.getMessage("error.phone.already-exist", null, locale));
        }

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    operations.delete("verificationCode_" + verificationCodeRequest.getPhone());
                    operations.opsForValue().set("verificationCode_" + verificationCodeRequest.getPhone(), verificationCode, phoneVerificationValidMinute, TimeUnit.MINUTES);

                    String smsBody = messageSource.getMessage("phone.verification.body", new String[]{verificationCode}, locale);
                    smsSender.sendOneMessage(verificationCodeRequest.getPhone(), smsBody);

                    return operations.exec();
                }catch(Exception e) {
                    log.error("Failed to send verification code", e);
                    operations.discard();
                    throw new SendSMSFailureException(messageSource.getMessage("error.send-sms.fail", new String[]{verificationCodeRequest.getPhone()}, locale));
                }
            }
        });
    }

    public void verifyEmailVerificationCode(EmailVerificationCodeRequest verificationCodeRequest) {
        Locale locale = LocaleContextHolder.getLocale();

        String key = "verificationCode_" + verificationCodeRequest.getEmail();
        String verificationCode = String.valueOf(redisTemplate.opsForValue().get(key));

        if(StringUtils.isEmpty(verificationCode) || !verificationCode.equals(verificationCodeRequest.getVerificationCode())) {
            throw new BadVerificationCodeException(messageSource.getMessage("error.email-verification-code.incorrect", null, locale));
        }
        redisTemplate.delete(key);
    }

    @Transactional
    public void verifyPhoneVerificationCode(String userId, PhoneVerificationCodeRequest verificationCodeRequest) {
        Locale locale = LocaleContextHolder.getLocale();

        String key = "verificationCode_" + verificationCodeRequest.getPhone();
        String verificationCode = String.valueOf(redisTemplate.opsForValue().get(key));

        if(StringUtils.isEmpty(verificationCode) || !verificationCode.equals(verificationCodeRequest.getVerificationCode())) {
            throw new BadVerificationCodeException(messageSource.getMessage("error.phone-verification-code.incorrect", null, locale));
        }
        User user = userService.getUser(userId);
        user.changeUserPhone(verificationCodeRequest.getPhone());
        redisTemplate.delete(key);
    }

    // 이메일 혹은 휴대전화로 인증코드를 보내는 메서드
    private String createVerificationCode(VERIFICATION_CODE_TYPE type) {
        String verificationCode;
        switch (type) {
            case EMAIL:
                verificationCode = UUID.randomUUID().toString().replace("-", "");
                break;
            case PHONE:
                verificationCode = RandomStringUtils.randomNumeric(8);
                break;
            default:
                verificationCode = null;
        }
        return verificationCode;
    }

}
