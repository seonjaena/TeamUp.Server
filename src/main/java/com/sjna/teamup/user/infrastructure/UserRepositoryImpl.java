package com.sjna.teamup.user.infrastructure;

import com.sjna.teamup.common.domain.exception.UserIdNotFoundException;
import com.sjna.teamup.user.domain.User;
import com.sjna.teamup.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User getUserByAccountId(String userId) {
        return userJpaRepository.findByAccountId(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Not Found. userId=" + userId)).toDomain();
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public User saveAndFlush(User user) {
        return userJpaRepository.saveAndFlush(UserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public boolean isExistsAccountId(String userId) {
        return userJpaRepository.existsByAccountId(userId);
    }

    @Override
    public boolean isExistsNickname(String userNickname) {
        return userJpaRepository.existsByNickname(userNickname);
    }

    @Override
    public boolean isExistsPhone(String phone) {
        return userJpaRepository.existsByPhone(phone);
    }

}
