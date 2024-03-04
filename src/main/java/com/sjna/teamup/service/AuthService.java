package com.sjna.teamup.service;

import com.sjna.teamup.dto.JwtDto;
import com.sjna.teamup.dto.request.LoginRequest;
import com.sjna.teamup.dto.response.LoginResponse;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRefreshToken;
import com.sjna.teamup.exception.UnAuthenticatedException;
import com.sjna.teamup.repository.UserRefreshTokenRepository;
import com.sjna.teamup.security.JwtProvider;
import com.sjna.teamup.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    @PersistenceContext
    private EntityManager em;

    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MessageSource messageSource;

    @Transactional
    public LoginResponse login(LoginRequest loginRequestDto) throws NoSuchAlgorithmException {
        User dbUser = userService.getUser(loginRequestDto.getUserId());
        if(!passwordEncoder.matches(loginRequestDto.getUserPw(), dbUser.getPw())) {
            log.warn("Password is incorrect. userId={}", loginRequestDto.getUserId());
            throw new UnAuthenticatedException(
                    messageSource.getMessage("error.password.incorrect",
                            new String[] {},
                            LocaleContextHolder.getLocale())
            );
        }

        JwtDto jwt = jwtProvider.createToken(dbUser.getId(), List.of(dbUser.getRole().getName()));
        // refresh token의 인덱스를 생성한다. (클라이언트에 전달)
        String refreshTokenIdxHash = StringUtil.getMd5(System.currentTimeMillis() + dbUser.getId());

        // 이미 refresh token이 있다면 삭제하고 저장, 없다면 그냥 저장
        Optional<UserRefreshToken> savedRefreshToken = userRefreshTokenRepository.findByUser(dbUser);
        if(savedRefreshToken.isPresent()) {
            userRefreshTokenRepository.delete(savedRefreshToken.get());
            em.flush();
        }
        userRefreshTokenRepository.save(UserRefreshToken.from(refreshTokenIdxHash, dbUser, jwt.getRefreshToken()));

        return new LoginResponse(jwt.getAccessToken(), refreshTokenIdxHash);
    }

    public String refreshAccessToken(String refreshTokenIdxHash) {
        UserRefreshToken refreshToken = userRefreshTokenRepository.findByIdxHash(refreshTokenIdxHash)
                .orElseThrow(() -> new UnAuthenticatedException(
                        messageSource.getMessage("notice.re-login.request",
                                new String[] {},
                                LocaleContextHolder.getLocale())
                ));

        String userId = jwtProvider.getUserId(refreshToken.getValue());
        List<String> userRoles = jwtProvider.getUserRoles(refreshToken.getValue());

        return jwtProvider.refreshAccessToken(userId, userRoles);
    }

}
