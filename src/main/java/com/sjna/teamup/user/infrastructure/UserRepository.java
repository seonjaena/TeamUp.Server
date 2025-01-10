package com.sjna.teamup.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAccountIdAndStatus(String userId, USER_STATUS userStatus);
    Optional<UserEntity> findByAccountId(String userId);
    Optional<UserEntity> findByNickname(String userNickname);
    Optional<UserEntity> findByPhone(String phone);

}
