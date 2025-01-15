package com.sjna.teamup.resume.infrastructure;

import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.resume.domain.ResumeLanguage;
import com.sjna.teamup.resume.service.port.ResumeLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ResumeLanguageRepositoryImpl implements ResumeLanguageRepository {

    private final ResumeLanguageJpaRepository resumeLanguageJpaRepository;

    @Override
    public List<ResumeLanguage> saveAllAndFlush(List<ResumeLanguage> resumeLanguages) {
         return resumeLanguageJpaRepository.saveAllAndFlush(resumeLanguages.stream().map(ResumeLanguageEntity::fromDomain).collect(Collectors.toList()))
                 .stream().map(ResumeLanguageEntity::toDomain)
                 .collect(Collectors.toList());
    }

    @Override
    public List<ResumeLanguage> findAllByResume(Resume resume) {
        return resumeLanguageJpaRepository.findAllByResume(ResumeEntity.fromDomain(resume))
                .stream().map(ResumeLanguageEntity::toDomain)
                .collect(Collectors.toList());
    }
}
