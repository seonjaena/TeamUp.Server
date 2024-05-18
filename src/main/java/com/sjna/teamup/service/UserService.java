package com.sjna.teamup.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sjna.teamup.dto.request.ChangePasswordRequest;
import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.dto.response.ProfileImageUrlResponse;
import com.sjna.teamup.dto.response.TestResponse;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRole;
import com.sjna.teamup.entity.enums.USER_STATUS;
import com.sjna.teamup.exception.*;
import com.sjna.teamup.repository.UserRepository;
import com.sjna.teamup.repository.UserRoleRepository;
import com.sjna.teamup.security.AuthUser;
import com.sjna.teamup.sender.EmailSender;
import com.sjna.teamup.security.EncryptionProvider;
import io.micrometer.common.util.StringUtils;
import com.sjna.teamup.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    @Value("${front.base-url}")
    private String frontBaseUrl;

    @Value("${service.email.change-password.valid-minute:60}")
    private Integer changePasswordValidMinute;

    @Value("${service.min-age:15}")
    private Integer minAge;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${service.profile-image.default-path}")
    private String defaultProfileImagePath;

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailSender emailSender;
    private final EncryptionProvider encryptionProvider;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final AmazonS3Client s3Client;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user;
        try {
            user = getUser(userId);
        }catch(UserIdNotFoundException e) {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("error.user-id-pw.incorrect",
                            new String[] {},
                            LocaleContextHolder.getLocale())
            );
        }

        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return new AuthUser(user.getAccountId(), user.getAccountPw(), roles, user);
    }

    public User getUser(String userId) {
        return getOptionalUser(userId)
                .orElseThrow(() -> new UserIdNotFoundException(
                        messageSource.getMessage("error.user-id.incorrect",
                                new String[] {},
                                LocaleContextHolder.getLocale())
                        )
                );
    }

    public Optional<User> getOptionalUser(String userId) {
        return userRepository.findByAccountId(userId);
    }

    public boolean checkUserIdAvailable(String userId) {
        return userRepository.findByAccountId(userId).isEmpty();
    }

    public boolean checkUserPhoneAvailable(String phone) {
        return userRepository.findByPhone(phone).isEmpty();
    }

    public boolean checkUserNicknameAvailable(String userNickname) {
        return userRepository.findByNickname(userNickname).isEmpty();
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {

        Locale locale = LocaleContextHolder.getLocale();

        String tempNickname = signUpRequest.getEmail().substring(0, signUpRequest.getEmail().indexOf("@")) + "_" + RandomStringUtils.randomAlphanumeric(5);

        if(userRepository.findByAccountId(signUpRequest.getEmail()).isPresent()) {
            throw new AlreadyUserEmailExistsException(messageSource.getMessage("error.email.already-exist", null, locale));
        }

        if(!signUpRequest.getUserPw().equals(signUpRequest.getUserPw2())) {
            throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
        }

        UserRole basicRole = userRoleRepository.findAll(Sort.by(Sort.Direction.ASC, "priority")).stream().findFirst()
                .orElseThrow(() -> new UserRoleNotExistException(
                        messageSource.getMessage("error.common.500",
                                new String[] {},
                                LocaleContextHolder.getLocale()
                        )
                ));

        User user = User.builder()
                .accountId(signUpRequest.getEmail())
                .accountPw(passwordEncoder.encode(signUpRequest.getUserPw()))
                .nickname(tempNickname)
                .role(basicRole)
                .status(USER_STATUS.NORMAL)
                .name(signUpRequest.getName())
                .build();

        userRepository.save(user);
    }

    public void sendChangePasswordUrl(String userId) {
        Locale locale = LocaleContextHolder.getLocale();
        User user = getUser(userId);
        String randomValue = UUID.randomUUID().toString();
        String url = makeChangePasswordUrl(user.getAccountId(), randomValue);

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    operations.delete("changePwdRandomValue_" + user.getAccountId());
                    operations.opsForValue().set("changePwdRandomValue_" + user.getAccountId(), randomValue, changePasswordValidMinute, TimeUnit.MINUTES);

                    String emailSubject = messageSource.getMessage("email.changePwd.subject", null, locale);
                    String emailBody = messageSource.getMessage("email.changePwd.body", new String[]{url, String.valueOf(changePasswordValidMinute)}, locale);
                    emailSender.sendRawEmail(List.of(user.getAccountId()), emailSubject, emailBody);

                    return operations.exec();
                }catch(SendEmailFailureException e) {
                    log.error("Failed to send change password link", e);
                    operations.discard();
                    throw e;
                }
            }
        });
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Locale locale = LocaleContextHolder.getLocale();

        String userId = encryptionProvider.decrypt(changePasswordRequest.getRandomValue1());
        log.info("decrypted userId = {}", userId);

        String randomValue = (String) redisTemplate.opsForValue().get("changePwdRandomValue_" + userId);

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    if(!Objects.equals(randomValue, changePasswordRequest.getRandomValue2())) {
                        throw new BadUrlChangePwException(messageSource.getMessage("error.change-pw.bad-url", null, locale));
                    }

                    if(!Objects.equals(changePasswordRequest.getUserPw(), changePasswordRequest.getUserPw2())) {
                        throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
                    }

                    operations.delete("changePwdRandomValue_" + userId);
                    changeUserPw(userId, changePasswordRequest.getUserPw());

                    return operations.exec();
                }catch(Exception e) {
                    log.error("Failed to change password", e);
                    operations.discard();
                    throw e;
                }
            }
        });
    }

    @Transactional
    public void changeNickname(String userId, String userNickname) {
        Locale locale = LocaleContextHolder.getLocale();

        User user = getUser(userId);
        // 이미 사용하고 있는 닉네임을 사용하는 경우 DB에 업데이트 할 필요 없음 (에러를 낼 필요도 없음)
        if(Objects.equals(userNickname, user.getNickname())) {
            return;
        }

        if(!checkUserNicknameAvailable(userNickname)) {
            throw new AlreadyUserNicknameExistsException(messageSource.getMessage("error.nickname.already-exist", null, locale));
        }

        user.changeUserNickname(userNickname);
        userRepository.save(user);
    }

    @Transactional
    public void changeBirth(String userId, LocalDate userBirth) {
        Locale locale = LocaleContextHolder.getLocale();

        if(DateUtil.isExistDate(userBirth)) {
            throw new UserBirthDateNotExistsException(messageSource.getMessage("error.date.not-exist", null, locale));
        }

        if(!DateUtil.isOlderThanOrEqual(userBirth, minAge)) {
            throw new UserYoungException(messageSource.getMessage("error.age.too-young", new Integer[]{minAge}, locale));
        }

        User user = getUser(userId);
        user.changeBirth(userBirth);
        userRepository.save(user);
    }

    public ProfileImageUrlResponse getProfileImageUrl(String userId) {
        User user = getUser(userId);
        String imageFullRoute = user.getProfileImage();
        String fileUrl;

        if(imageFullRoute != null) {
            fileUrl = getFilePreSignedUrl(bucket, imageFullRoute);
        }else {
            fileUrl = getFilePreSignedUrl(bucket, defaultProfileImagePath);
        }

        return new ProfileImageUrlResponse(fileUrl);
    }

    // 테스트 용도의 임시 메서드
    public TestResponse a(String userId) {
        User user = getUser(userId);
        String nickname = user.getNickname();
        String phone = user.getPhone();
        LocalDate birth = user.getBirth();
        String profileImage = user.getProfileImage();
        String fileUrl;

        if(profileImage != null) {
            fileUrl = getFilePreSignedUrl(bucket, profileImage);
        }else {
            fileUrl = getFilePreSignedUrl(bucket, defaultProfileImagePath);
        }

        TestResponse testResponse = new TestResponse();
        testResponse.setProfileImageUrl(fileUrl);
        testResponse.setNickname(nickname);
        testResponse.setBirth(birth);
        testResponse.setPhone(phone);
        return testResponse;
    }

    private String getFilePreSignedUrl(String bucketName, String s3FileFullPath) {

        Locale locale = LocaleContextHolder.getLocale();

        if(!isFileExistsInStorage(bucketName, s3FileFullPath)) {
            throw new FileNotExistsException(messageSource.getMessage("error.file.not-exist", null, locale));
        }

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, s3FileFullPath)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        String preSignedURL = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        log.info("pre-signed url = {}", preSignedURL);
        return preSignedURL;
    }

    private boolean isFileExistsInStorage(String bucketName, String s3FileFullPath) {
        if(StringUtils.isEmpty(s3FileFullPath)) {
            return false;
        }
        return s3Client.doesObjectExist(bucketName, s3FileFullPath);
    }

    private void changeUserPw(String userId, String newUserPw) {
        User user = getUser(userId);
        user.changeUserPassword(passwordEncoder.encode(newUserPw));
        userRepository.saveAndFlush(user);
    }

    private String makeChangePasswordUrl(String userId, String randomValue) {
        String encryptedUserId = encryptionProvider.encrypt(userId);
        return String.format("%s/account/changePwd?random1=%s&random2=%s", frontBaseUrl, encryptedUserId, randomValue);
    }

}
