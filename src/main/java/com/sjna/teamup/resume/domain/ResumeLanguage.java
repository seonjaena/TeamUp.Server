package com.sjna.teamup.resume.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumeLanguage {

    private Long id;
    private Resume resume;
    private String type;
    private Short grade;

    public static ResumeLanguage from(Resume resume, String type, Short grade) {
        return ResumeLanguage.builder()
                .resume(resume)
                .type(type)
                .grade(grade)
                .build();
    }

}
