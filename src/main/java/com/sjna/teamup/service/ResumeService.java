package com.sjna.teamup.service;

import com.sjna.teamup.dto.request.AddResumeRequest;
import com.sjna.teamup.dto.response.ResumeResponse;
import com.sjna.teamup.entity.Resume;
import com.sjna.teamup.entity.ResumeLanguage;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.exception.ResumeNotFoundException;
import com.sjna.teamup.repository.ResumeLanguageRepository;
import com.sjna.teamup.repository.ResumeRepository;
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
        User user = userService.getNotDeletedUser(userId);
        
        String projectUrls = resumeRequest.getProjectUrls() == null ? "" : String.join(";", resumeRequest.getProjectUrls());
        String certificates = resumeRequest.getCertificates() == null ? "" : String.join(";", resumeRequest.getCertificates());

        Resume resume = Resume.builder()
                .introduction(resumeRequest.getIntroduction())
                .projectUrl(projectUrls)
                .skill(resumeRequest.getSkill())
                .experience(resumeRequest.getExperience())
                .additionalInfo(resumeRequest.getAdditionalInfo())
                .certificate(certificates)
                .user(user)
                .build();
        Resume savedResume = resumeRepository.saveAndFlush(resume);
        List<ResumeLanguage> resumeLanguages = new ArrayList<>();
        for(Map.Entry<String, String> language : resumeRequest.getLanguages().entrySet()) {
            resumeLanguages.add(ResumeLanguage.of(savedResume, language.getKey(), Short.valueOf(language.getValue())));
        }
        List<ResumeLanguage> savedResumeLanguages = resumeLanguageRepository.saveAllAndFlush(resumeLanguages);

        for(ResumeLanguage language : savedResumeLanguages) {
            savedResume.addLanguage(language);
        }
    }

    public ResumeResponse getResume(String userId) {
        User user = userService.getNotDeletedUser(userId);
        Resume resume = resumeRepository.findByUser(user).orElseThrow(() -> new ResumeNotFoundException(
                messageSource.getMessage("error.resume.not-found", null, LocaleContextHolder.getLocale())
        ));
        List<ResumeLanguage> resumeLanguages = resumeLanguageRepository.findAllByResume(resume);
        return new ResumeResponse(resume, resumeLanguages);
    }

}
