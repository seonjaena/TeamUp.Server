package com.sjna.teamup.resume.service.port;

import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.resume.domain.ResumeLanguage;
import java.util.List;

public interface ResumeLanguageRepository {

    List<ResumeLanguage> saveAllAndFlush(List<ResumeLanguage> resumeLanguages);
    List<ResumeLanguage> findAllByResume(Resume resume);

}
