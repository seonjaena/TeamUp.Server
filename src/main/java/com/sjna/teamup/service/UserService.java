package com.sjna.teamup.service;

import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRole;
import com.sjna.teamup.entity.enums.USER_STATUS;
import com.sjna.teamup.exception.UserRoleNotExistException;
import com.sjna.teamup.repository.UserRepository;
import com.sjna.teamup.repository.UserRoleRepository;
import com.sjna.teamup.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = getUser(userId);

        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return new AuthUser(user.getId(), user.getPw(), roles, user);
    }

    public User getUser(String userId) {
        return userRepository.findByIdAndStatus(userId, USER_STATUS.NORMAL).
                orElseThrow(() -> new UsernameNotFoundException("Can't find userId. userId=" + userId));
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {

        UserRole basicRole = userRoleRepository.findAll(Sort.by(Sort.Direction.DESC, "priority")).stream().findFirst()
                .orElseThrow(() -> new UserRoleNotExistException("System doesn't have role. Please add role."));

        User user = User.builder()
                .id(signUpRequest.getUserId())
                .pw(passwordEncoder.encode(signUpRequest.getUserPw()))
                .email(signUpRequest.getEmail())
                .nickname(signUpRequest.getUserNickname())
                .role(basicRole)
                .status(USER_STATUS.NORMAL)
                .name(signUpRequest.getName())
                .birth(signUpRequest.getBirth())
                .phone(signUpRequest.getPhone())
                .build();

        userRepository.save(user);
    }

}
