package com.sjna.teamup.repository;

import com.sjna.teamup.entity.Resume;
import com.sjna.teamup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByUser(User user);

}
