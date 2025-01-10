package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, Long> {
    Optional<UserRefreshTokenEntity> findByUser(UserEntity user);
    Optional<UserRefreshTokenEntity> findByIdxHash(String idxHash);
}
