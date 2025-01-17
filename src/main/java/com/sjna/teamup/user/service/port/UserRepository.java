package com.sjna.teamup.user.service.port;

import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.user.domain.User;

import java.util.Optional;

public interface UserRepository {

    User getUserByAccountId(String userId);
    User save(User user);
    User saveAndFlush(User user);
    boolean isExistsAccountId(String userId);
    boolean isExistsNickname(String userNickname);
    boolean isExistsPhone(String phone);

}
