package com.sjna.teamup.dto.response;

import com.sjna.teamup.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserProfileInfoResponse {

    private String imageUrl;
    private String userId;
    private String userName;
    private LocalDate userBirth;
    private String userNickname;
    private String userPhone;

    public UserProfileInfoResponse(User user) {
        this.userId = user.getAccountId();
        this.userName = user.getName();
        this.userBirth = user.getBirth();
        this.userNickname = user.getNickname();
        this.userPhone = user.getPhone();
    }

}
