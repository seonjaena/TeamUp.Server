package com.sjna.teamup.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RESUME")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Resume {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INTRODUCTION", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "PROJECT_URL", columnDefinition = "TEXT")
    private String projectUrl;

    @Column(name = "SKILL", columnDefinition = "TEXT")
    private String skill;

    @Column(name = "EXPERIENCE", columnDefinition = "TEXT")
    private String experience;

    @Column(name = "ADDITIONAL_INFO", columnDefinition = "TEXT")
    private String additionalInfo;

    @Column(name = "CERTIFICATE", length = 2000)
    private String certificate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))     // RDBMS에서 외래키를 설정하기 위해서 사용 (NO_CONSTRAINT: 논리적으로만 연관관계 매핑)
    private User user;

    @OneToMany(mappedBy = "resume")
    private List<ResumeLanguage> languages = new ArrayList<>();

    public void addLanguage(ResumeLanguage resumeLanguage) {
        this.languages.add(resumeLanguage);
    }

    @Builder
    public Resume(String introduction, String projectUrl, String skill, String experience, String additionalInfo, String certificate, User user, List<ResumeLanguage> languages) {
        this.introduction = introduction;
        this.projectUrl = projectUrl;
        this.skill = skill;
        this.experience = experience;
        this.additionalInfo = additionalInfo;
        this.certificate = certificate;
        this.user = user;
        this.languages = languages == null ? new ArrayList<>() : languages;
    }

}
