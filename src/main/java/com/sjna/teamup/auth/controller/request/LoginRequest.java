package com.sjna.teamup.auth.controller.request;

import com.sjna.teamup.common.controller.constraint.UserIdConstraint;
import com.sjna.teamup.common.controller.constraint.UserPwConstraint;
import lombok.Data;

@Data
public class LoginRequest {

    @UserIdConstraint
    private String userId;

    @UserPwConstraint
    private String userPw;

}
