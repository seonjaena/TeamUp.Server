package com.sjna.teamup.auth.service;

import com.sjna.teamup.auth.controller.port.UserTokenService;
import com.sjna.teamup.auth.controller.response.RefreshAccessTokenResponse;
import com.sjna.teamup.auth.domain.UserRefreshToken;
import com.sjna.teamup.auth.service.port.UserRefreshTokenRepository;
import com.sjna.teamup.common.security.JwtProvider;
import com.sjna.teamup.common.util.StringUtil;
import com.sjna.teamup.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserTokenServiceImpl implements UserTokenService {

    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void deleteRefreshTokenByUser(User user) {
        Optional<UserRefreshToken> refreshToken = userRefreshTokenRepository.findByUser(user);
        if(refreshToken.isPresent()) {
            userRefreshTokenRepository.delete(refreshToken.get());
        }
    }

    @Transactional
    public RefreshAccessTokenResponse refreshAccessToken(String refreshTokenIdxHash) throws NoSuchAlgorithmException {
        // Refresh Token의 위치를 나타내는 해시 값을 통해 Refresh Token을 DB에서 찾음
        UserRefreshToken refreshToken = userRefreshTokenRepository.getByIdxHash(refreshTokenIdxHash);

        // TODO: Refresh Token의 만료 시간을 확인하고 만료되었으면 에러를 발생시키는 로직 추가

        // Refresh Token을 통해서 사용자 ID와 Role을 얻어옴
        String userId = jwtProvider.getUserId(refreshToken.getValue());
        List<String> userRoles = jwtProvider.getUserRoles(refreshToken.getValue());

        // hash만 바꿔서 DB Update 참고: https://brunch.co.kr/@anonymdevoo/37
        String newRefreshTokenIdxHash = StringUtil.getMd5(System.currentTimeMillis() + userId);
        refreshToken.changeIdxHash(newRefreshTokenIdxHash);
        userRefreshTokenRepository.save(refreshToken);

        // Access Token 재발급
        return new RefreshAccessTokenResponse(jwtProvider.refreshAccessToken(userId, userRoles), newRefreshTokenIdxHash);
    }

}
