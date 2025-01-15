package com.sjna.teamup.resume.infrastructure;

import com.sjna.teamup.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeJpaRepository extends JpaRepository<ResumeEntity, Long> {

    Optional<ResumeEntity> findByUser(UserEntity user);

}
