package com.sjna.teamup.repository;

import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    Optional<UserRefreshToken> findByUser(User user);
    Optional<UserRefreshToken> findByIdxHash(String idxHash);
}
