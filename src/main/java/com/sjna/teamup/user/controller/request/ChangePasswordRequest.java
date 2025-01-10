package com.sjna.teamup.user.controller.request;

import com.sjna.teamup.common.controller.constraint.UserPwConstraint;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @UserPwConstraint
    private String userPw;

    @UserPwConstraint
    private String userPw2;

    private String randomValue1;
    private String randomValue2;

}
