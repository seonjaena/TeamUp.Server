package com.sjna.teamup.user.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sjna.teamup.auth.controller.port.UserRoleService;
import com.sjna.teamup.auth.controller.port.UserTokenService;
import com.sjna.teamup.common.domain.exception.*;
import com.sjna.teamup.user.controller.port.UserService;
import com.sjna.teamup.user.controller.request.ChangePasswordRequest;
import com.sjna.teamup.user.controller.request.LoginChangePasswordRequest;
import com.sjna.teamup.user.controller.request.SignUpRequest;
import com.sjna.teamup.user.controller.response.ProfileImageUrlResponse;
import com.sjna.teamup.user.controller.response.UserProfileInfoResponse;
import com.sjna.teamup.user.domain.User;
import com.sjna.teamup.common.domain.FILTER_INCLUSION_MODE;
import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.common.service.port.MailSender;
import com.sjna.teamup.common.security.EncryptionProvider;
import com.sjna.teamup.user.service.port.UserRepository;
import io.micrometer.common.util.StringUtils;
import com.sjna.teamup.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Value("${front.base-url}")
    private String frontBaseUrl;

    @Value("${service.email.change-password.valid-minute:60}")
    private Integer changePasswordValidMinute;

    @Value("${service.min-age:15}")
    private Short minAge;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${service.profile-image.default-path}")
    private String defaultProfileImagePath;

    @Value("${service.profile-image.temp-dir}")
    private String profileImageTempDir;

    @Value("${service.profile-image.permanent-dir}")
    private String profileImagePermanentDir;

    @Value("${service.zone-id:Asia/Seoul}")
    private String serviceZoneId;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MailSender mailSender;
    private final EncryptionProvider encryptionProvider;
    private final UserRepository userRepository;
    private final UserTokenService userTokenService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final AmazonS3Client s3Client;

    public User getUser(String userId) {
        return userRepository.getUserByAccountId(userId);
    }

    public User getUser(String userId, USER_STATUS[] userStatuses, FILTER_INCLUSION_MODE filterInclusionMode) {
        User user = getUser(userId);
        boolean statusMatched = Arrays.stream(userStatuses)
                .anyMatch(status -> user.getStatus() == status);

        // TODO: 이 분기 로직을 별도의 클래스 혹은 메서드로 뺄 것인지 확인 필요
        switch (filterInclusionMode) {
            case INCLUDE:
                if(statusMatched) {
                    return user;
                }
                break;
            case EXCLUDE:
                if(!statusMatched) {
                    return user;
                }
                break;
        }

        throw new UserIdNotFoundException(
                messageSource.getMessage("error.user-id.incorrect", null, LocaleContextHolder.getLocale())
        );
    }

    public User getNotDeletedUser(String userId) {
        return getUser(userId, new USER_STATUS[]{ USER_STATUS.DELETED }, FILTER_INCLUSION_MODE.EXCLUDE);
    }

    public boolean checkUserIdAvailable(String userId) {
        return !userRepository.isExistsAccountId(userId);
    }

    public boolean checkUserPhoneAvailable(String phone) {
        return !userRepository.isExistsPhone(phone);
    }

    public boolean checkUserNicknameAvailable(String userNickname) {
        return !userRepository.isExistsNickname(userNickname);
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {

        Locale locale = LocaleContextHolder.getLocale();

        /**
         * 임시 닉네임 생성
         * 임시 닉네임 양식. {사용자의 이메일의 '@' 앞 부분}_{랜덤한 알파벳 혹은 숫자 5자리}
         */
        // TODO: 테스트를 위해 어떻게 하면 좋을지 고민
        String tempNickname = signUpRequest.getEmail().substring(0, signUpRequest.getEmail().indexOf("@")) + "_" + RandomStringUtils.randomAlphanumeric(5);

        // 동일한 이메일이 이미 존재하는지 확인
        if(!checkUserIdAvailable(signUpRequest.getEmail())) {
            throw new AlreadyUserEmailExistsException(messageSource.getMessage("error.email.already-exist", null, locale));
        }

        // 사용자가 입력한 비밀번호와 비밀번호 확인이 동일한지 확인
        // TODO: null 확인 로직 필요한지 고민 필요
        if(!signUpRequest.getUserPw().equals(signUpRequest.getUserPw2())) {
            throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
        }

        // 사용자의 권한을 가장 낮은 권한으로 세팅 (TODO: 권한에 대한 내용을 나중에 어떻게 활용할 것인지 상세하게 설정해야 함)
        User user = User.builder()
                .accountId(signUpRequest.getEmail())
                .accountPw(passwordEncoder.encode(signUpRequest.getUserPw()))
                .nickname(tempNickname)
                .role(userRoleService.getBasic())
                .status(USER_STATUS.NORMAL)
                .name(signUpRequest.getName())
                .build();

        // 사용자 정보 저장 (회원가입 성공)
        userRepository.save(user);
    }

    public void sendChangePasswordUrl(String userId) {
        Locale locale = LocaleContextHolder.getLocale();
        User user = getNotDeletedUser(userId);
        // TODO: 랜덤한 URL을 만드는 코드를 별도의 인터페이스와 클래스로 구현 (테스트 용이성)
        String randomValue = UUID.randomUUID().toString();
        // 비밀번호 수정을 위한 URL 생성
        String url = makeChangePasswordUrl(user.getAccountId(), randomValue);

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    /**
                     * 인증코드 Redis에 저장 (이미 존재한다면 제거하고 저장)
                     * Redis에 저장되는 인증 코드 양식. key=changePwdRandomValue_{사용자 이메일}, value={인증코드}, 유효기간=60분(수정 가능)
                     */
                    operations.delete("changePwdRandomValue_" + user.getAccountId());
                    operations.opsForValue().set("changePwdRandomValue_" + user.getAccountId(), randomValue, changePasswordValidMinute, TimeUnit.MINUTES);

                    // TODO: 이메일의 내용에 해당 URL의 만료시간을 공지해야 함
                    String emailSubject = messageSource.getMessage("email.changePwd.subject", null, locale);
                    String emailBody = messageSource.getMessage("email.changePwd.body", new String[]{url, String.valueOf(changePasswordValidMinute)}, locale);
                    mailSender.sendRawEmail(List.of(user.getAccountId()), emailSubject, emailBody);

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
    public void findPassword(ChangePasswordRequest changePasswordRequest) {
        Locale locale = LocaleContextHolder.getLocale();

        // 사용자가 보낸 랜덤값1을 복호화 (사용자 ID)
        String userId = encryptionProvider.decrypt(changePasswordRequest.getRandomValue1());
        log.info("decrypted userId = {}", userId);

        // Redis에서 복호화된 사용자 ID를 Key로 하는 인증 값을 찾음
        String randomValue = (String) redisTemplate.opsForValue().get("changePwdRandomValue_" + userId);

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    // 사용자가 보낸 랜덤값2와 저장된 인증 값이 동일한지 확인
                    if(StringUtils.isEmpty(randomValue) || !randomValue.equals(changePasswordRequest.getRandomValue2())) {
                        throw new BadUrlChangePwException(messageSource.getMessage("error.change-pw.bad-url", null, locale));
                    }

                    User user = getNotDeletedUser(userId);

                    // 사용자가 보낸 변경할 비밀번호와 변경할 비밀번호 확인이 동일한지 확인
                    if(changePasswordRequest.getUserPw() == null || !changePasswordRequest.getUserPw().equals(changePasswordRequest.getUserPw2())) {
                        throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
                    }

                    // 기존에 사용하던 비밀번호와 동일한 비밀번호로 변경하려고 하면 에러 발생
                    if(passwordEncoder.matches(changePasswordRequest.getUserPw(), user.getAccountPw())) {
                        throw new AlreadyUsingPassword(messageSource.getMessage("error.pw.already-using", null, locale));
                    }

                    // Redis에서 인증 값 제거 (인증 완료)
                    operations.delete("changePwdRandomValue_" + userId);
                    // 사용자 비밀번호 수정
                    // TODO: 시간 바꾸는 기능을 밖으로 빼고 따로 인터페이스와 클래스로 구현 (테스트 용이성)
                    user.changeUserPassword(serviceZoneId, passwordEncoder.encode(changePasswordRequest.getUserPw()));
                    userRepository.saveAndFlush(user);

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
    public void changePassword(String userId, LoginChangePasswordRequest changePasswordRequest) {
        Locale locale = LocaleContextHolder.getLocale();

        User user = getNotDeletedUser(userId);

        String oldPw = changePasswordRequest.getOldPw();
        String userPw = changePasswordRequest.getUserPw();
        String userPw2 = changePasswordRequest.getUserPw2();

        // TODO: isBlank vs isEmpty 하나로 통일
        if(StringUtils.isBlank(oldPw) || !passwordEncoder.matches(oldPw, user.getAccountPw())) {
            throw new OriginPasswordIncorrect(messageSource.getMessage("error.user-pw.incorrect", null, locale));
        }

        if(!userPw.equals(userPw2)) {
            throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
        }

        if(oldPw.equals(userPw)) {
            throw new AlreadyUsingPassword(messageSource.getMessage("error.pw.already-using", null, locale));
        }

        user.changeUserPassword(serviceZoneId, passwordEncoder.encode(userPw));
        userRepository.save(user);
    }

    @Transactional
    public void changeNickname(String userId, String userNickname) {
        Locale locale = LocaleContextHolder.getLocale();

        User user = getNotDeletedUser(userId);
        // 본인이 이미 사용하고 있는 닉네임을 사용하는 경우 DB에 업데이트 할 필요 없음 (에러를 낼 필요도 없음)
        if(StringUtils.isBlank(userNickname) || userNickname.equals(user.getNickname())) {
            return;
        }

        // 만약 이미 존재하는 사용자의 닉네임으로 변경할 경우 막음
        if(!checkUserNicknameAvailable(userNickname)) {
            throw new AlreadyUserNicknameExistsException(messageSource.getMessage("error.nickname.already-exist", null, locale));
        }

        // 사용자 닉네임 수정
        user.changeUserNickname(userNickname);
        userRepository.save(user);
    }

    @Transactional
    public void changeBirth(String userId, LocalDate userBirth) {
        Locale locale = LocaleContextHolder.getLocale();

        // 실제로 존재하는 날짜인지 검사
        if(!DateUtil.isExistDate(userBirth)) {
            throw new UserBirthDateNotExistsException(messageSource.getMessage("error.date.not-exist", null, locale));
        }

        // 나이가 너무 적은지 확인
        // TODO: 비정상적인 나이도 확인 ex: 300살
        if(!DateUtil.isOlderThanOrEqual(userBirth, minAge)) {
            throw new UserYoungException(messageSource.getMessage("error.age.too-young", new Short[]{minAge}, locale));
        }

        // 사용자 생년월일 수정
        User user = getNotDeletedUser(userId);
        user.changeBirth(userBirth);
        userRepository.save(user);
    }

    @Transactional
    public void changeProfileImage(String userId, MultipartFile profileImage) {
        // TODO: 테스트할 때 Locale이 Spring 객체라 귀찮을 것 같음
        Locale locale = LocaleContextHolder.getLocale();
        // 유저가 전송한 파일 검사
        validateProfileImage(locale, profileImage);

        String fileName = profileImage.getOriginalFilename();
        String fileExtension = FilenameUtils.getExtension(fileName);
        String tempFileName = String.format("%s.%s", UUID.randomUUID(), fileExtension);

        try {
            // 임시 파일 생성
            File file = new File(String.format("%s/%s", profileImageTempDir, tempFileName));
            file.mkdirs();
            profileImage.transferTo(file);

            String permanentName = String.format("%s/%s", profileImagePermanentDir, tempFileName);

            // 사용자 프로필 이미지 주소를 변경
            User user = getNotDeletedUser(userId);
            user.changeProfileImage(permanentName);
            userRepository.save(user);

            // S3에 파일 업로드
            // TODO: 테스트 환경에서는 s3가 없는데 어떻게 테스트할 것인지?
            s3Client.putObject(bucket, permanentName, file);

            // 임시 파일 삭제
            file.delete();
        }catch(IOException e) {
            throw new ChangeProfileImageFailureException(messageSource.getMessage("error.change-profile-image.fail", null, locale));
        }
    }

    @Transactional
    public void changeUserPhone(String userId, String userPhone) {
        User user = getNotDeletedUser(userId);
        user.changeUserPhone(userPhone);
        userRepository.save(user);
    }

    public ProfileImageUrlResponse getProfileImageUrl(String userId) {
        // 사용자의 프로필 이미지가 저장된 경로 알아옴
        User user = getNotDeletedUser(userId);
        String imageFullRoute = user.getProfileImage();

        String fileUrl;

        // 프로필 이미지가 존재한다면 해당 이미지의 임시 URL 반환, 존재하지 않는다면 기본 이미지의 임시 URL 반환
        if(imageFullRoute != null) {
            fileUrl = getFilePreSignedUrl(bucket, imageFullRoute);
        }else {
            fileUrl = getFilePreSignedUrl(bucket, defaultProfileImagePath);
        }

        return new ProfileImageUrlResponse(fileUrl);
    }

    public UserProfileInfoResponse getProfileInfo(String userId) {
        User user = getNotDeletedUser(userId);
        UserProfileInfoResponse userProfileDto = new UserProfileInfoResponse(user);

        String profileImage = user.getProfileImage();
        String fileUrl;

        if(profileImage != null) {
            fileUrl = getFilePreSignedUrl(bucket, profileImage);
        }else {
            fileUrl = getFilePreSignedUrl(bucket, defaultProfileImagePath);
        }

        userProfileDto.setImageUrl(fileUrl);
        return userProfileDto;
    }

    @Transactional
    public void delete(String userId) {
        User user = getNotDeletedUser(userId);
        userTokenService.deleteRefreshTokenByUser(user);
        user.delete();
    }

    // TODO: S3에 완전히 의존적인 메서드이기 때문에 테스트할 경우 어려움이 있을 수 있음. 어떻게 할 것인지 고민 필요.
    private String getFilePreSignedUrl(String bucketName, String s3FileFullPath) {

        Locale locale = LocaleContextHolder.getLocale();

        // 파일이 존재하지 않는다면 에러 발생
        if(!isFileExistsInStorage(bucketName, s3FileFullPath)) {
            throw new FileNotExistsException(messageSource.getMessage("error.file.not-exist", null, locale));
        }

        // 임시 URL의 만료 시간 설정 (5분)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);

        // 임시 URL 생성 후 반환
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, s3FileFullPath)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        String preSignedURL = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        log.debug("pre-signed url = {}", preSignedURL);
        return preSignedURL;
    }

    // TODO: S3에 완전히 의존적인 메서드이기 때문에 테스트할 경우 어려움이 있을 수 있음. 어떻게 할 것인지 고민 필요. 그리고 private 메서드는 테스트가 불가능 함.
    private boolean isFileExistsInStorage(String bucketName, String s3FileFullPath) {
        if(StringUtils.isEmpty(s3FileFullPath)) {
            return false;
        }
        return s3Client.doesObjectExist(bucketName, s3FileFullPath);
    }

    // TODO: private 메서드는 테스트가 불가능함.
    private String makeChangePasswordUrl(String userId, String randomValue) {
        String encryptedUserId = encryptionProvider.encrypt(userId);
        // Query Parameter로 사용자 ID를 암호화 한 값과 인증값을 넘기도록 함
        return String.format("%s/account/changePwd?random1=%s&random2=%s", frontBaseUrl, encryptedUserId, randomValue);
    }

    // TODO: private 메서드는 테스트가 불가능함.
    private void validateProfileImage(Locale locale, MultipartFile profileImage) {
        short minFileMB = 0;
        short maxFileMB = 5;
        List<String> allowedExtensions = List.of("png", "jpg", "jpeg", "gif");

        // 파일이 존재하는지 검사
        if(profileImage == null || profileImage.isEmpty()) {
            throw new EmptyFileException(messageSource.getMessage("error.file.empty", null, locale));
        }

        long fileSize = profileImage.getSize();

        // 파일의 크기가 0MB 보다 크고 5MB 이하 인지 확인
        if(fileSize <= minFileMB * 1048576L || fileSize > maxFileMB * 1048576L) {
            throw new FileSizeException(messageSource.getMessage("error.file-size.not-proper", new Short[]{minFileMB, maxFileMB}, locale));
        }

        if( !allowedExtensions.contains( FilenameUtils.getExtension(profileImage.getOriginalFilename()) ) ) {
            throw new BadFileExtensionException(messageSource.getMessage("error.file-extension-not-proper", new String[] {allowedExtensions.toString()}, locale));
        }

    }

}
