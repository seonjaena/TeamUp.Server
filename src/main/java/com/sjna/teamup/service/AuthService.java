package com.sjna.teamup.service;

import com.sjna.teamup.dto.JwtDto;
import com.sjna.teamup.dto.request.LoginRequest;
import com.sjna.teamup.dto.request.VerificationCodeRequest;
import com.sjna.teamup.dto.response.LoginResponse;
import com.sjna.teamup.dto.response.RefreshAccessTokenResponse;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRefreshToken;
import com.sjna.teamup.entity.enums.VERIFICATION_CODE_TYPE;
import com.sjna.teamup.exception.*;
import com.sjna.teamup.repository.UserRefreshTokenRepository;
import com.sjna.teamup.security.JwtProvider;
import com.sjna.teamup.sender.EmailSender;
import com.sjna.teamup.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    @Value("${service.email.verification.valid-minute:10}")
    private Integer emailVerificationValidMinute;

    @PersistenceContext
    private EntityManager em;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailSender emailSender;
    private final MessageSource messageSource;

    @Transactional
    public LoginResponse login(LoginRequest loginRequestDto) throws NoSuchAlgorithmException {
        User dbUser = userService.getUser(loginRequestDto.getUserId());
        if(!passwordEncoder.matches(loginRequestDto.getUserPw(), dbUser.getAccountPw())) {
            log.warn("Password is incorrect. userId={}", loginRequestDto.getUserId());
            throw new UnAuthenticatedException(
                    messageSource.getMessage("error.password.incorrect",
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

    public void sendVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        Locale locale = LocaleContextHolder.getLocale();
        String verificationCode = createVerficationCode(verificationCodeRequest);

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
                    operations.opsForValue().set("verificationCode_" + verificationCodeRequest.getEmail(), verificationCode);

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

    public void verifyVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        Locale locale = LocaleContextHolder.getLocale();

        String key;
        String verificationCode;

        switch(verificationCodeRequest.getVerificationCodeType()) {
            case VERIFICATION_CODE_TYPE.EMAIL:
                key = "verificationCode_" + verificationCodeRequest.getEmail();
                verificationCode = String.valueOf(redisTemplate.opsForValue().get(key));
                break;
            case VERIFICATION_CODE_TYPE.PHONE:
                key = "verificationCode_" + verificationCodeRequest.getPhone();
                verificationCode = String.valueOf(redisTemplate.opsForValue().get(key));
                break;
            default:
                throw new UnknownVerificationCodeException(messageSource.getMessage(
                        "error.verification-code-type.unknown",
                        new String[] {},
                        LocaleContextHolder.getLocale())
                );
        }

        if(StringUtils.isEmpty(verificationCode) || !verificationCode.equals(verificationCodeRequest.getVerificationCode())) {
            throw new BadVerificationCodeException(messageSource.getMessage("error.email-verification-code.incorrect", null, locale));
        }
        redisTemplate.delete(key);
    }

    // 이메일 혹은 휴대전화로 인증코드를 보내는 메서드
    private String createVerficationCode(VerificationCodeRequest verificationCodeRequest) {
        String verificationCode;

        switch(verificationCodeRequest.getVerificationCodeType()) {
            case VERIFICATION_CODE_TYPE.EMAIL:
                verificationCodeRequest.setPhone(null);
                verificationCode = UUID.randomUUID().toString().replace("-", "");
                break;
            case VERIFICATION_CODE_TYPE.PHONE:
                verificationCodeRequest.setEmail(null);
                verificationCode = StringUtil.getVerification6DigitCode();
                break;
            default:
                throw new UnknownVerificationCodeException(messageSource.getMessage(
                        "error.verification-code-type.unknown",
                        new String[] {},
                        LocaleContextHolder.getLocale())
                );
        }

        return verificationCode;
    }

}
