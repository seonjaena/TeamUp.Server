package com.sjna.teamup.dto.request;

import com.sjna.teamup.validator.constraint.UserIdConstraint;
import com.sjna.teamup.validator.constraint.UserNameConstraint;
import com.sjna.teamup.validator.constraint.UserPwConstraint;
import lombok.Data;

@Data
public class SignUpRequest {

    @UserNameConstraint
    private String name;

    @UserIdConstraint
    private String email;

    @UserPwConstraint
    private String userPw;

    @UserPwConstraint
    private String userPw2;

}
