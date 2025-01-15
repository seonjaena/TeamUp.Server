package com.sjna.teamup.user.service.port;

import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.user.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByAccountIdAndStatus(String userId, USER_STATUS userStatus);
    Optional<User> findActiveUserByAccountId(String userId);
    Optional<User> findByAccountId(String userId);
    User getUserByAccountId(String userId);
    User save(User user);
    User saveAndFlush(User user);
    void delete(User user);
    boolean isExistsAccountId(String userId);
    boolean isExistsNickname(String userNickname);
    boolean isExistsPhone(String phone);

}
