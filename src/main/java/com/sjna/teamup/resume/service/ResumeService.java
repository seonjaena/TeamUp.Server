package com.sjna.teamup.resume.service;

import com.sjna.teamup.resume.controller.request.AddResumeRequest;
import com.sjna.teamup.resume.controller.response.ResumeResponse;
import com.sjna.teamup.resume.infrastructure.ResumeEntity;
import com.sjna.teamup.resume.infrastructure.ResumeLanguageEntity;
import com.sjna.teamup.user.infrastructure.UserEntity;
import com.sjna.teamup.common.domain.exception.ResumeNotFoundException;
import com.sjna.teamup.resume.infrastructure.ResumeLanguageRepository;
import com.sjna.teamup.resume.infrastructure.ResumeRepository;
import com.sjna.teamup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final MessageSource messageSource;
    private final UserService userService;
    private final ResumeRepository resumeRepository;
    private final ResumeLanguageRepository resumeLanguageRepository;

    @Transactional
    public void addResume(AddResumeRequest resumeRequest, String userId) {
        UserEntity userEntity = userService.getNotDeletedUser(userId);
        
        String projectUrls = resumeRequest.getProjectUrls() == null ? "" : String.join(";", resumeRequest.getProjectUrls());
        String certificates = resumeRequest.getCertificates() == null ? "" : String.join(";", resumeRequest.getCertificates());

        ResumeEntity resumeEntity = ResumeEntity.builder()
                .introduction(resumeRequest.getIntroduction())
                .projectUrl(projectUrls)
                .skill(resumeRequest.getSkill())
                .experience(resumeRequest.getExperience())
                .additionalInfo(resumeRequest.getAdditionalInfo())
                .certificate(certificates)
                .user(userEntity)
                .build();
        ResumeEntity savedResumeEntity = resumeRepository.saveAndFlush(resumeEntity);
        List<ResumeLanguageEntity> resumeLanguageEntities = new ArrayList<>();
        for(Map.Entry<String, String> language : resumeRequest.getLanguages().entrySet()) {
            resumeLanguageEntities.add(ResumeLanguageEntity.of(savedResumeEntity, language.getKey(), Short.valueOf(language.getValue())));
        }
        List<ResumeLanguageEntity> savedResumeLanguageEntities = resumeLanguageRepository.saveAllAndFlush(resumeLanguageEntities);

        for(ResumeLanguageEntity language : savedResumeLanguageEntities) {
            savedResumeEntity.addLanguage(language);
        }
    }

    public ResumeResponse getResume(String userId) {
        UserEntity userEntity = userService.getNotDeletedUser(userId);
        ResumeEntity resumeEntity = resumeRepository.findByUser(userEntity).orElseThrow(() -> new ResumeNotFoundException(
                messageSource.getMessage("error.resume.not-found", null, LocaleContextHolder.getLocale())
        ));
        List<ResumeLanguageEntity> resumeLanguageEntities = resumeLanguageRepository.findAllByResume(resumeEntity);
        return new ResumeResponse(resumeEntity, resumeLanguageEntities);
    }

}
