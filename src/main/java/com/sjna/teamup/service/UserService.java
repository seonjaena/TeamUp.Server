package com.sjna.teamup.service;

import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.dto.request.VerificationCodeRequest;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRole;
import com.sjna.teamup.entity.UserVerificationCode;
import com.sjna.teamup.entity.enums.USER_STATUS;
import com.sjna.teamup.entity.enums.VERIFICATION_CODE_TYPE;
import com.sjna.teamup.exception.UnknownVerificationCodeException;
import com.sjna.teamup.exception.UserRoleNotExistException;
import com.sjna.teamup.repository.UserRepository;
import com.sjna.teamup.repository.UserRoleRepository;
import com.sjna.teamup.repository.UserVerificationCodeRepository;
import com.sjna.teamup.security.AuthUser;
import com.sjna.teamup.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserVerificationCodeRepository userVerificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = getUser(userId);

        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return new AuthUser(user.getAccountId(), user.getAccountPw(), roles, user);
    }

    public User getUser(String userId) {
        return userRepository.findByAccountIdAndStatus(userId, USER_STATUS.NORMAL).
                orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("error.user-id-pw.incorrect",
                                new String[] {},
                                LocaleContextHolder.getLocale())
                        )
                );
    }

    public boolean checkUserIdAvailable(String userId) {
        return userRepository.findByAccountId(userId).isEmpty();
    }

    public boolean checkUserNicknameAvailable(String userNickname) {
        return userRepository.findByNickname(userNickname).isEmpty();
    }

    @Transactional
    public void saveVerificationCode(VerificationCodeRequest verificationCodeRequest) {

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

        UserVerificationCode userVerificationCode = UserVerificationCode.builder()
                .phone(verificationCodeRequest.getPhone())
                .email(verificationCodeRequest.getEmail())
                .verificationCode(verificationCode)
                .type(verificationCodeRequest.getVerificationCodeType())
                .build();
        userVerificationCodeRepository.save(userVerificationCode);
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {

        UserRole basicRole = userRoleRepository.findAll(Sort.by(Sort.Direction.DESC, "priority")).stream().findFirst()
                .orElseThrow(() -> new UserRoleNotExistException(
                        messageSource.getMessage("error.common.500",
                                new String[] {},
                                LocaleContextHolder.getLocale()
                        )
                ));

        String tempNickname = signUpRequest.getEmail().substring(0, signUpRequest.getEmail().indexOf("@")) + "_" + RandomStringUtils.randomAlphanumeric(5);

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

}
