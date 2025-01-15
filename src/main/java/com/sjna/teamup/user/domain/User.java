package com.sjna.teamup.user.domain;

import com.sjna.teamup.auth.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class User {

    private Long idx;
    private String accountId;
    private String accountPw;
    private String nickname;
    private UserRole role;
    private USER_STATUS status;
    private String name;
    private LocalDate birth;
    private String phone;
    private String profileImage;
    private LocalDateTime lastAccountPwModified;

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

    public void changeUserPassword(String serviceZoneId, String userPw) {
        this.accountPw = userPw;
        this.lastAccountPwModified = LocalDateTime.now(ZoneId.of(serviceZoneId));
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getName()));
    }

}
