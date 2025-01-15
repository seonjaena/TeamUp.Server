package com.sjna.teamup.resume.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeLanguageJpaRepository extends JpaRepository<ResumeLanguageEntity, Long> {

    List<ResumeLanguageEntity> findAllByResume(ResumeEntity resume);

}
