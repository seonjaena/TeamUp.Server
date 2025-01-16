package com.sjna.teamup.resume.domain;

import com.sjna.teamup.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
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

    public void addLanguage(ResumeLanguage language) {
        this.languages.add(language);
    }

}
