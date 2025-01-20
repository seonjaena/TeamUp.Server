package com.sjna.teamup.resume.service;

import com.sjna.teamup.common.domain.exception.ResumeNotFoundException;
import com.sjna.teamup.common.service.port.LocaleHolder;
import com.sjna.teamup.resume.controller.port.ResumeService;
import com.sjna.teamup.resume.controller.request.AddResumeRequest;
import com.sjna.teamup.resume.controller.response.ResumeResponse;
import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.resume.domain.ResumeLanguage;
import com.sjna.teamup.resume.service.port.ResumeLanguageRepository;
import com.sjna.teamup.resume.service.port.ResumeRepository;
import com.sjna.teamup.user.controller.port.UserService;
import com.sjna.teamup.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeServiceImpl implements ResumeService {

    private final UserService userService;
    private final ResumeRepository resumeRepository;
    private final ResumeLanguageRepository resumeLanguageRepository;
    private final MessageSource messageSource;
    private final LocaleHolder localeHolder;

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
            resumeLanguages.add(ResumeLanguage.from(savedResume, language.getKey(), Short.valueOf(language.getValue())));
        }
        List<ResumeLanguage> savedResumeLanguages = resumeLanguageRepository.saveAllAndFlush(resumeLanguages);

        for(ResumeLanguage language : savedResumeLanguages) {
            savedResume.addLanguage(language);
        }
    }

    public ResumeResponse getResume(String userId) {
        User user = userService.getNotDeletedUser(userId);
        Resume resume;
        try {
            resume = resumeRepository.getByUser(user);
        }catch(ResumeNotFoundException e) {
            log.warn(e.getMessage());
            throw new ResumeNotFoundException(
                    messageSource.getMessage("error.resume.not-found", null, localeHolder.getLocale()));
        }

        List<ResumeLanguage> resumeLanguages = resumeLanguageRepository.findAllByResume(resume);
        return new ResumeResponse(resume, resumeLanguages);
    }

}
