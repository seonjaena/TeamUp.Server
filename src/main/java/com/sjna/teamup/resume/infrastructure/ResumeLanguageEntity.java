package com.sjna.teamup.resume.infrastructure;

import com.sjna.teamup.resume.domain.ResumeLanguage;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RESUME_LANGUAGE")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeLanguageEntity {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESUME_IDX", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))     // RDBMS에서 외래키를 설정하기 위해서 사용 (NO_CONSTRAINT: 논리적으로만 연관관계 매핑)
    private ResumeEntity resume;

    @Column(name = "TYPE", length = 200)
    private String type;

    @Column(name = "GRADE")
    private Short grade;

    public static ResumeLanguageEntity fromDomain(ResumeLanguage resumeLanguage) {
        ResumeLanguageEntity resumeLanguageEntity = new ResumeLanguageEntity();
        resumeLanguageEntity.id = resumeLanguage.getId();
        resumeLanguageEntity.resume = ResumeEntity.fromDomain(resumeLanguage.getResume());
        resumeLanguageEntity.type = resumeLanguage.getType();
        resumeLanguageEntity.grade = resumeLanguage.getGrade();
        return resumeLanguageEntity;
    }

    public ResumeLanguage toDomain() {
        return ResumeLanguage.builder()
                .id(this.id)
                .resume(this.resume.toDomain())
                .type(this.type)
                .grade(this.grade)
                .build();
    }

}
