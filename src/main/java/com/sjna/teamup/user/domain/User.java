package com.sjna.teamup.user.domain;

import com.sjna.teamup.auth.domain.UserRole;
import com.sjna.teamup.common.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void changeUserPassword(String userPw, ClockHolder clockHolder) {
        this.accountPw = userPw;
        this.lastAccountPwModified = clockHolder.getCurrentDateTime();
    }

    public String getChangePasswordUrl(String frontBaseUrl, String randomValue1, String randomValue2) {
        // Query Parameter로 사용자 ID를 암호화 한 값과 인증값을 넘기도록 함
        return String.format("%s/account/changePwd?random1=%s&random2=%s", frontBaseUrl, randomValue1, randomValue2);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getName()));
    }

}
