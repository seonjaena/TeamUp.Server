package com.sjna.teamup.user.infrastructure;

import com.sjna.teamup.common.domain.exception.UserIdNotFoundException;
import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.user.domain.User;
import com.sjna.teamup.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final MessageSource messageSource;

    @Override
    public User getUserByAccountId(String userId) {
        return userJpaRepository.findByAccountId(userId)
                .orElseThrow(() -> new UserIdNotFoundException(
                        messageSource.getMessage("error.user-id.incorrect", null, LocaleContextHolder.getLocale())
                )).toDomain();
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
    public void delete(User user) {
        userJpaRepository.delete(UserEntity.fromDomain(user));
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
