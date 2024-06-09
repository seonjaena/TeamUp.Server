package com.sjna.teamup.dto.request;

import com.sjna.teamup.validator.constraint.UserIdConstraint;
import com.sjna.teamup.validator.constraint.UserPwConstraint;
import lombok.Data;

@Data
public class LoginRequest {

    @UserIdConstraint
    private String userId;

    @UserPwConstraint
    private String userPw;

}
