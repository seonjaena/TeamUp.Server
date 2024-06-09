package com.sjna.teamup.dto.request;

import com.sjna.teamup.validator.constraint.UserPwConstraint;
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
