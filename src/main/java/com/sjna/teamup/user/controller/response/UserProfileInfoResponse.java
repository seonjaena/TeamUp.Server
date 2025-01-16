package com.sjna.teamup.user.controller.response;

import com.sjna.teamup.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileInfoResponse {

    private String imageUrl;
    private String userId;
    private String userName;
    private LocalDate userBirth;
    private String userNickname;
    private String userPhone;
    private LocalDateTime lastPwdModifiedDateTime;

    public UserProfileInfoResponse(User user) {
        this.userId = user.getAccountId();
        this.userName = user.getName();
        this.userBirth = user.getBirth();
        this.userNickname = user.getNickname();
        this.userPhone = user.getPhone();
        this.lastPwdModifiedDateTime = user.getLastAccountPwModified();
    }

}
