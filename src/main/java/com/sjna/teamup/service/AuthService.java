package com.sjna.teamup.service;

import com.sjna.teamup.dto.JwtDto;
import com.sjna.teamup.dto.request.Login;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.exception.UnAuthenticatedException;
import com.sjna.teamup.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public JwtDto login(Login loginRequestDto) {
        User dbUser = userService.getUser(loginRequestDto.getUserId());
        if(!passwordEncoder.matches(loginRequestDto.getUserPw(), dbUser.getPw())) {
            throw new UnAuthenticatedException("Password is incorrect. userId = " + loginRequestDto.getUserId());
        }
        return jwtProvider.createToken(dbUser.getId(), List.of(dbUser.getRole().getName()));
    }

    public String getRefreshToken(String userId) {
        User user = userService.getUser(userId);
        return user.getRefreshToken();
    }

}
