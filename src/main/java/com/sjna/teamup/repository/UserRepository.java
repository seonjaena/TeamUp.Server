package com.sjna.teamup.repository;

import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.enums.USER_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccountIdAndStatus(String userId, USER_STATUS userStatus);
    Optional<User> findByAccountId(String userId);
    Optional<User> findByNickname(String userNickname);

}
