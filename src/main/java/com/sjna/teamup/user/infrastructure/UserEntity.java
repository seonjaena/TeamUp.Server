package com.sjna.teamup.user.infrastructure;

import com.sjna.teamup.auth.infrastructure.UserRoleEntity;
import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity{

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "ACCOUNT_ID", length = 100)
    private String accountId;

    @Column(name = "ACCOUNT_PW", length = 100)
    private String accountPw;

    @Column(name = "NICKNAME", length = 100)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))     // RDBMS에서 외래키를 설정하기 위해서 사용 (NO_CONSTRAINT: 논리적으로만 연관관계 매핑)
    private UserRoleEntity role;

    @Column(name = "STATUS")
    @Convert(converter = USER_STATUS.Converter.class)
    private USER_STATUS status;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "BIRTH")
    private LocalDate birth;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "PROFILE_IMAGE", length = 200)
    private String profileImage;

    @Column(name = "LAST_ACCOUNT_PW_MODIFIED")
    private LocalDateTime lastAccountPwModified;

    public static UserEntity fromDomain(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.idx = user.getIdx();
        userEntity.accountId = user.getAccountId();
        userEntity.accountPw = user.getAccountPw();
        userEntity.nickname = user.getNickname();
        userEntity.role = UserRoleEntity.fromDomain(user.getRole());
        userEntity.status = user.getStatus();
        userEntity.name = user.getName();
        userEntity.birth = user.getBirth();
        userEntity.phone = user.getPhone();
        userEntity.profileImage = user.getProfileImage();
        userEntity.lastAccountPwModified = user.getLastAccountPwModified();
        return userEntity;
    }

    public User toDomain() {
        return User.builder()
                .idx(this.idx)
                .accountId(this.accountId)
                .accountPw(this.accountPw)
                .nickname(this.nickname)
                .role(this.role.toDomain())
                .status(this.status)
                .name(this.name)
                .birth(this.birth)
                .phone(this.phone)
                .profileImage(this.profileImage)
                .lastAccountPwModified(this.lastAccountPwModified)
                .build();
    }

}
