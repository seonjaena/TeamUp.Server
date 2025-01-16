package com.sjna.teamup.user.infrastructure;

import com.sjna.teamup.user.domain.USER_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAccountId(String userId);
    boolean existsByAccountId(String userId);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);

}
