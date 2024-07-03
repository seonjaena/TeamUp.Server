package com.sjna.teamup.repository;

import com.sjna.teamup.entity.Resume;
import com.sjna.teamup.entity.ResumeLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeLanguageRepository extends JpaRepository<ResumeLanguage, Long> {

    List<ResumeLanguage> findAllByResume(Resume resume);

}
