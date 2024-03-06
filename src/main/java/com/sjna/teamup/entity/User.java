package com.sjna.teamup.entity;

import com.sjna.teamup.entity.enums.USER_STATUS;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "ACCOUNT_ID", length = 100)
    private String accountId;

    @Column(name = "ACCOUNT_PW", length = 100)
    private String accountPw;

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

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "BIRTH")
    private LocalDate birth;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Builder
    public User(String accountId, String accountPw, String email, String nickname, UserRole role, USER_STATUS status, String name, LocalDate birth, String phone) {
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
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
