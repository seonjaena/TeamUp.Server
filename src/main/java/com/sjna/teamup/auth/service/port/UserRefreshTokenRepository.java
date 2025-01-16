package com.sjna.teamup.auth.service.port;

import com.sjna.teamup.auth.domain.UserRefreshToken;
import com.sjna.teamup.user.domain.User;
import java.util.Optional;

public interface UserRefreshTokenRepository {

    Optional<UserRefreshToken> findByUser(User user);
    void delete(UserRefreshToken userRefreshToken);
    void deleteAndFlush(UserRefreshToken userRefreshToken);
    UserRefreshToken save(UserRefreshToken userRefreshToken);
    UserRefreshToken getByIdxHash(String idxHash);

}
