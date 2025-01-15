package com.sjna.teamup.resume.domain;

import com.sjna.teamup.user.domain.User;
import java.util.ArrayList;
import java.util.List;

public class Resume {

    private Long id;
    private String introduction;
    private String projectUrl;
    private String skill;
    private String experience;
    private String additionalInfo;
    private String certificate;
    private User user;
    private List<ResumeLanguage> languages = new ArrayList<>();

    public void addLanguage(ResumeLanguage resumeLanguage) {
        this.languages.add(resumeLanguage);
    }

}
