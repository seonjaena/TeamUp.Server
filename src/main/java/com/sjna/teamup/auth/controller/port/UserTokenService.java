package com.sjna.teamup.auth.controller.port;

import com.sjna.teamup.auth.controller.response.RefreshAccessTokenResponse;
import com.sjna.teamup.user.domain.User;
import java.security.NoSuchAlgorithmException;

public interface UserTokenService {

    void deleteRefreshTokenByUser(User user);
    RefreshAccessTokenResponse refreshAccessToken(String refreshTokenIdxHash) throws NoSuchAlgorithmException;

}
