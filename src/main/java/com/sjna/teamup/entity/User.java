package com.sjna.teamup.entity;

import com.sjna.teamup.entity.enums.USER_STATUS;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "ID", length = 100)
    private String id;

    @Column(name = "PW", length = 100)
    private String pw;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "NICKNAME", length = 100)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))     // RDBMS에서 외래키를 설정하기 위해서 사용 (NO_CONSTRAINT: 논리적으로만 연관관계 매핑)
    private UserRole role;

    @Column(name = "STATUS")
    @Convert(converter = USER_STATUS.Converter.class)
    private USER_STATUS status;

    @Column(name = "REFRESH_TOKEN", length = 100)
    private String refreshToken;

}
