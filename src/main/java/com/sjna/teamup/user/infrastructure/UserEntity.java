package com.sjna.teamup.user.infrastructure;

import com.sjna.teamup.auth.infrastructure.UserRoleEntity;
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
public class UserEntity implements UserDetails {

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

    public void changeUserPassword(String serviceZoneId, String userPw) {
        this.accountPw = userPw;
        this.lastAccountPwModified = LocalDateTime.now(ZoneId.of(serviceZoneId));
    }
    public void changeUserNickname(String userNickname) {
        this.nickname = userNickname;
    }
    public void changeBirth(LocalDate birth) {
        this.birth = birth;
    }
    public void changeUserPhone(String phone) {
        this.phone = phone;
    }
    public void changeProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void delete() {
        this.status = USER_STATUS.DELETED;
    }

    /**
     * UserDetails 상속 받아서 생기는 코드
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getName()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
