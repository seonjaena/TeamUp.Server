package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.auth.domain.UserRefreshToken;
import com.sjna.teamup.auth.service.port.UserRefreshTokenRepository;
import com.sjna.teamup.common.domain.exception.UnAuthenticatedException;
import com.sjna.teamup.user.domain.User;
import com.sjna.teamup.user.infrastructure.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRefreshTokenRepositoryImpl implements UserRefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    private final UserRefreshTokenJpaRepository userRefreshTokenJpaRepository;
    private final MessageSource messageSource;

    @Override
    public Optional<UserRefreshToken> findByUser(User user) {
        return userRefreshTokenJpaRepository.findByUser(UserEntity.fromDomain(user))
                .map(UserRefreshTokenEntity::toDomain);
    }

    @Override
    public void delete(UserRefreshToken userRefreshToken) {
        userRefreshTokenJpaRepository.delete(UserRefreshTokenEntity.fromDomain(userRefreshToken));
    }

    @Override
    public void deleteAndFlush(UserRefreshToken userRefreshToken) {
        userRefreshTokenJpaRepository.delete(UserRefreshTokenEntity.fromDomain(userRefreshToken));
        em.flush();
    }

    @Override
    public UserRefreshToken save(UserRefreshToken userRefreshToken) {
        return userRefreshTokenJpaRepository.save(UserRefreshTokenEntity.fromDomain(userRefreshToken)).toDomain();
    }

    @Override
    public UserRefreshToken getByIdxHash(String idxHash) {
        return userRefreshTokenJpaRepository.findByIdxHash(idxHash)
                .orElseThrow(() -> new UnAuthenticatedException(
                        messageSource.getMessage("notice.re-login.request", null, LocaleContextHolder.getLocale())
                )).toDomain();
    }
}
