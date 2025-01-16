package com.sjna.teamup.resume.controller.response;

import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.resume.domain.ResumeLanguage;
import com.sjna.teamup.resume.infrastructure.ResumeEntity;
import com.sjna.teamup.resume.infrastructure.ResumeLanguageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
public class ResumeResponse {

    private String introduction;
    private List<String> projectUrls = new ArrayList<>();
    private String skill;
    private String experience;
    private String additionalInfo;
    private List<String> certificates = new ArrayList<>();
    private Map<String, Short> languages = new HashMap<>();

    public ResumeResponse(Resume resume, List<ResumeLanguage> resumeLanguages) {
        this.introduction = resume.getIntroduction();
        this.projectUrls = resume.getProjectUrl() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(resume.getProjectUrl().split(";")));
        this.skill = resume.getSkill();
        this.experience = resume.getExperience();
        this.additionalInfo = resume.getAdditionalInfo();
        this.certificates = resume.getCertificate() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(resume.getCertificate().split(";")));
        for(ResumeLanguage language : resumeLanguages) {
            this.languages.put(language.getType(), language.getGrade());
        }
    }

}
