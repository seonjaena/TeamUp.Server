package com.sjna.teamup.resume.infrastructure;

import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "RESUME")
public class ResumeEntity {

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
    private UserEntity user;

    public static ResumeEntity fromDomain(Resume resume) {
        ResumeEntity resumeEntity = new ResumeEntity();
        resumeEntity.id = resume.getId();
        resumeEntity.introduction = resume.getIntroduction();
        resumeEntity.projectUrl = resume.getProjectUrl();
        resumeEntity.skill = resume.getSkill();
        resumeEntity.experience = resume.getExperience();
        resumeEntity.additionalInfo = resume.getAdditionalInfo();
        resumeEntity.certificate = resume.getCertificate();
        resumeEntity.user = UserEntity.fromDomain(resume.getUser());
        return resumeEntity;
    }

    public Resume toDomain() {
        return Resume.builder()
                .id(this.id)
                .introduction(this.introduction)
                .projectUrl(this.projectUrl)
                .skill(this.skill)
                .experience(this.experience)
                .additionalInfo(this.additionalInfo)
                .certificate(this.certificate)
                .user(this.user.toDomain())
                .build();
    }

}
