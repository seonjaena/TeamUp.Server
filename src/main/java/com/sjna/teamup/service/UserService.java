package com.sjna.teamup.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sjna.teamup.dto.request.ChangePasswordRequest;
import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.dto.response.ProfileImageUrlResponse;
import com.sjna.teamup.dto.response.UserProfileInfoResponse;
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
import org.apache.commons.io.FilenameUtils;
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

    @Value("${service.profile-image.temp-dir}")
    private String profileImageTempDir;

    @Value("${service.profile-image.permanent-dir}")
    private String profileImagePermanentDir;

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

        /**
         * 임시 닉네임 생성
         * 임시 닉네임 양식. {사용자의 이메일의 '@' 앞 부분}_{랜덤한 알파벳 혹은 숫자 5자리}
         */
        String tempNickname = signUpRequest.getEmail().substring(0, signUpRequest.getEmail().indexOf("@")) + "_" + RandomStringUtils.randomAlphanumeric(5);

        // 동일한 이메일이 이미 존재하는지 확인
        if(userRepository.findByAccountId(signUpRequest.getEmail()).isPresent()) {
            throw new AlreadyUserEmailExistsException(messageSource.getMessage("error.email.already-exist", null, locale));
        }

        // 사용자가 입력한 비밀번호와 비밀번호 확인이 동일한지 확인
        if(!signUpRequest.getUserPw().equals(signUpRequest.getUserPw2())) {
            throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
        }

        // 사용자의 권한을 가장 낮은 권한으로 세팅 (TODO: 권한에 대한 내용을 나중에 어떻게 활용할 것인지 상세하게 설정해야 함)
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

        // 사용자 정보 저장 (회원가입 성공)
        userRepository.save(user);
    }

    public void sendChangePasswordUrl(String userId) {
        Locale locale = LocaleContextHolder.getLocale();
        User user = getUser(userId);
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
                    if(!Objects.equals(randomValue, changePasswordRequest.getRandomValue2())) {
                        throw new BadUrlChangePwException(messageSource.getMessage("error.change-pw.bad-url", null, locale));
                    }

                    // 사용자가 보낸 변경할 비밀번호와 변경할 비밀번호 확인이 동일한지 확인
                    if(!Objects.equals(changePasswordRequest.getUserPw(), changePasswordRequest.getUserPw2())) {
                        throw new UserPwPw2DifferentException(messageSource.getMessage("error.pw-pw2.different", null, locale));
                    }

                    // Redis에서 인증 값 제거 (인증 완료)
                    operations.delete("changePwdRandomValue_" + userId);
                    // 사용자 비밀번호 수정
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
        // 본인이 이미 사용하고 있는 닉네임을 사용하는 경우 DB에 업데이트 할 필요 없음 (에러를 낼 필요도 없음)
        if(Objects.equals(userNickname, user.getNickname())) {
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
        if(!DateUtil.isOlderThanOrEqual(userBirth, minAge)) {
            throw new UserYoungException(messageSource.getMessage("error.age.too-young", new Integer[]{minAge}, locale));
        }

        // 사용자 생년월일 수정
        User user = getUser(userId);
        user.changeBirth(userBirth);
        userRepository.save(user);
    }

    @Transactional
    public void changeProfileImage(String userId, MultipartFile profileImage) {
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
            User user = getUser(userId);
            user.changeProfileImage(permanentName);
            userRepository.save(user);

            // S3에 파일 업로드
            s3Client.putObject(bucket, permanentName, file);

            // 임시 파일 삭제
            file.delete();
        }catch(IOException e) {
            throw new ChangeProfileImageFailureException(messageSource.getMessage("error.change-profile-image.fail", null, locale));
        }
    }

    public ProfileImageUrlResponse getProfileImageUrl(String userId) {
        // 사용자의 프로필 이미지가 저장된 경로 알아옴
        User user = getUser(userId);
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
        User user = getUser(userId);
        UserProfileInfoResponse userProfileDto = new UserProfileInfoResponse(user);

        String profileImage = user.getProfileImage();
        String fileUrl;

        if(user != null) {
            fileUrl = getFilePreSignedUrl(bucket, profileImage);
        }else {
            fileUrl = getFilePreSignedUrl(bucket, defaultProfileImagePath);
        }

        userProfileDto.setImageUrl(fileUrl);
        return userProfileDto;
    }

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
        // Query Parameter로 사용자 ID를 암호화 한 값과 인증값을 넘기도록 함
        return String.format("%s/account/changePwd?random1=%s&random2=%s", frontBaseUrl, encryptedUserId, randomValue);
    }

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
