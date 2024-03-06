package com.sjna.teamup.repository;

import com.sjna.teamup.entity.UserVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationCodeRepository extends JpaRepository<UserVerificationCode, Long> {
}
