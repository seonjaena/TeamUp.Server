package com.sjna.teamup.service;

import com.sjna.teamup.dto.request.AddResumeRequest;
import com.sjna.teamup.entity.Resume;
import com.sjna.teamup.entity.ResumeLanguage;
import com.sjna.teamup.entity.User;
import com.sjna.teamup.repository.ResumeLanguageRepository;
import com.sjna.teamup.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserService userService;
    private final ResumeRepository resumeRepository;
    private final ResumeLanguageRepository resumeLanguageRepository;

    @Transactional
    public void addResume(AddResumeRequest resumeRequest, String userId) {
        User user = userService.getNotDeletedUser(userId);
        Resume resume = Resume.builder()
                .introduction(resumeRequest.getIntroduction())
                .projectUrl(resumeRequest.getProjectUrl())
                .skill(resumeRequest.getSkill())
                .experience(resumeRequest.getExperience())
                .additionalInfo(resumeRequest.getAdditionalInfo())
                .certificate(resumeRequest.getCertificate())
                .user(user)
                .build();
        Resume savedResume = resumeRepository.saveAndFlush(resume);
        List<ResumeLanguage> resumeLanguages = new ArrayList<>();
        for(Map.Entry<String, Short> language : resumeRequest.getLanguages().entrySet()) {
            resumeLanguages.add(ResumeLanguage.of(savedResume, language.getKey(), language.getValue()));
        }
        List<ResumeLanguage> savedResumeLanguages = resumeLanguageRepository.saveAllAndFlush(resumeLanguages);

        for(ResumeLanguage language : savedResumeLanguages) {
            savedResume.addLanguage(language);
        }
    }

}
