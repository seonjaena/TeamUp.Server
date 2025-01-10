package com.sjna.teamup.user.controller.request;

import com.sjna.teamup.common.controller.constraint.UserPwConstraint;
import lombok.Data;

@Data
public class LoginChangePasswordRequest {

    @UserPwConstraint
    private String oldPw;

    @UserPwConstraint
    private String userPw;

    @UserPwConstraint
    private String userPw2;

}
