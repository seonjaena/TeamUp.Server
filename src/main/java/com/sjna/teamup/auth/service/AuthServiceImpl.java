package com.sjna.teamup.auth.service;

import com.sjna.teamup.auth.controller.port.AuthService;
import com.sjna.teamup.auth.domain.UserRefreshToken;
import com.sjna.teamup.auth.service.port.UserRefreshTokenRepository;
import com.sjna.teamup.common.domain.exception.*;
import com.sjna.teamup.common.domain.Jwt;
import com.sjna.teamup.auth.controller.request.LoginRequest;
import com.sjna.teamup.auth.controller.request.EmailVerificationCodeRequest;
import com.sjna.teamup.auth.controller.request.PhoneVerificationCodeRequest;
import com.sjna.teamup.auth.controller.response.LoginResponse;
import com.sjna.teamup.common.service.port.LocaleHolder;
import com.sjna.teamup.common.service.port.SmsSender;
import com.sjna.teamup.user.controller.port.UserService;
import com.sjna.teamup.user.domain.User;
import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.auth.domain.VERIFICATION_CODE_TYPE;
import com.sjna.teamup.common.security.JwtProvider;
import com.sjna.teamup.common.service.port.MailSender;
import com.sjna.teamup.common.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    @Value("${service.email.verification.valid-minute:10}")
    private Integer emailVerificationValidMinute;

    @Value("${service.phone.verification.valid-minute:10}")
    private Integer phoneVerificationValidMinute;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailSender mailSender;
    private final SmsSender smsSender;
    private final MessageSource messageSource;
    private final LocaleHolder localeHolder;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) throws NoSuchAlgorithmException {

        // 사용자 ID를 사용하여 사용자 정보 얻어옴
        User dbUser = userService.getUser(loginRequest.getUserId());

        // 사용자가 입력한 비밀번호와 DB에 저장된 비밀번호 비교
        if(!passwordEncoder.matches(loginRequest.getUserPw(), dbUser.getAccountPw())) {
            log.warn("Password is incorrect. userId={}", loginRequest.getUserId());
            throw new UnAuthenticatedException(
                    messageSource.getMessage("error.user-id-pw.incorrect", null, localeHolder.getLocale())
            );
        }

        if(dbUser.getStatus() == USER_STATUS.DELETED) {
            throw new DeletedUserException(messageSource.getMessage("notice.deleted-user", null, localeHolder.getLocale()));
        }

        // JWT Token 생성(Refresh, Access)
        Jwt jwt = jwtProvider.createToken(dbUser.getAccountId(), dbUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        // refresh token의 인덱스를 생성한다. (클라이언트에 전달)
        String refreshTokenIdxHash = StringUtil.getMd5(System.currentTimeMillis() + dbUser.getAccountId());

        // 이미 refresh token이 있다면 삭제하고 저장, 없다면 그냥 저장
        Optional<UserRefreshToken> savedRefreshToken = userRefreshTokenRepository.findByUser(dbUser);
        if(savedRefreshToken.isPresent()) {
            userRefreshTokenRepository.deleteAndFlush(savedRefreshToken.get());
        }
        userRefreshTokenRepository.save(UserRefreshToken.from(refreshTokenIdxHash, dbUser, jwt.getRefreshToken()));

        return new LoginResponse(jwt.getAccessToken(), refreshTokenIdxHash);
    }

    public void sendVerificationCode(EmailVerificationCodeRequest verificationCodeRequest) {
        Locale locale = localeHolder.getLocale();

        // 이메일 인증 코드 생성
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
                    /**
                     * 인증코드 Redis에 저장 (이미 존재한다면 제거하고 저장)
                     * Redis에 저장되는 인증 코드 양식. key=verificationCode_{사용자 이메일}, value={인증코드}, 유효기간=10분(수정 가능)
                     */
                    operations.delete("verificationCode_" + verificationCodeRequest.getEmail());
                    operations.opsForValue().set("verificationCode_" + verificationCodeRequest.getEmail(), verificationCode, emailVerificationValidMinute, TimeUnit.MINUTES);

                    // TODO: 이메일의 내용에 해당 인증 코드의 만료시간을 공지해야 함
                    String emailSubject = messageSource.getMessage("email.verification.subject", null, locale);
                    String emailBody = messageSource.getMessage("email.verification.body", new String[]{verificationCode, String.valueOf(emailVerificationValidMinute)}, locale);
                    // 사용자에게 이메일 전송
                    mailSender.sendRawEmail(List.of(verificationCodeRequest.getEmail()), emailSubject, emailBody);

                    return operations.exec();
                }catch(SendEmailFailureException e) {
                    log.error("Failed to send verification code", e);
                    operations.discard();
                    throw e;
                }
            }
        });

    }

    // TODO: 이메일 인증하는 부분과 비슷한 코드가 많기 때문에 리팩토링 필요
    public void sendVerificationCode(PhoneVerificationCodeRequest verificationCodeRequest) {
        Locale locale = localeHolder.getLocale();

        // 인증 코드 생성
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

                    /**
                     * 인증코드 Redis에 저장 (이미 존재한다면 제거하고 저장)
                     * Redis에 저장되는 인증 코드 양식. key=verificationCode_{사용자 전화번호}, value={인증코드}, 유효기간=10분(수정 가능)
                     */
                    operations.delete("verificationCode_" + verificationCodeRequest.getPhone());
                    operations.opsForValue().set("verificationCode_" + verificationCodeRequest.getPhone(), verificationCode, phoneVerificationValidMinute, TimeUnit.MINUTES);

                    // TODO: SMS의 내용에 해당 인증 코드의 만료시간을 공지해야 함
                    String smsBody = messageSource.getMessage("phone.verification.body", new String[]{verificationCode}, locale);

                    // SMS 전송
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

    // TODO: 핸드폰 인증코드 확인 코드랑 비슷하기 때문에 리팩토링 필요
    public void verifyEmailVerificationCode(EmailVerificationCodeRequest verificationCodeRequest) {
        Locale locale = localeHolder.getLocale();

        // Redis에 저장되는 인증 코드 양식. key=verificationCode_{사용자 이메일}
        String key = "verificationCode_" + verificationCodeRequest.getEmail();
        String verificationCode = String.valueOf(redisTemplate.opsForValue().get(key));

        // 사용자가 보낸 인증코드와 Redis에 저장된 인증 코드가 동일한지 확인
        if(StringUtils.isEmpty(verificationCode) || !verificationCode.equals(verificationCodeRequest.getVerificationCode())) {
            throw new BadVerificationCodeException(messageSource.getMessage("error.email-verification-code.incorrect", null, locale));
        }

        // Redis 데이터 제거
        redisTemplate.delete(key);
    }

    @Transactional
    public void verifyPhoneVerificationCode(String userId, PhoneVerificationCodeRequest verificationCodeRequest) {
        Locale locale = localeHolder.getLocale();

        // Redis에 저장되는 인증 코드 양식. key=verificationCode_{사용자 전화번호}
        String key = "verificationCode_" + verificationCodeRequest.getPhone();
        String verificationCode = String.valueOf(redisTemplate.opsForValue().get(key));

        // 사용자가 보낸 인증코드와 Redis에 저장된 인증 코드가 동일한지 확인
        if(StringUtils.isEmpty(verificationCode) || !verificationCode.equals(verificationCodeRequest.getVerificationCode())) {
            throw new BadVerificationCodeException(messageSource.getMessage("error.phone-verification-code.incorrect", null, locale));
        }

        // 사용자 휴대전화 번호 변경
        userService.changeUserPhone(userId, verificationCodeRequest.getPhone());

        // Redis에 저장된 인증 코드 제거
        redisTemplate.delete(key);
    }

    // 이메일 혹은 휴대전화로 인증코드를 보내는 메서드
    // TODO: 따로 인터페이스, 클래스로 빼야 함 (테스트 용이성)
    private String createVerificationCode(VERIFICATION_CODE_TYPE type) {
        String verificationCode;
        type.name();
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
