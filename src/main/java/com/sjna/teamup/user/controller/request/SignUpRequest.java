package com.sjna.teamup.user.controller.request;

import com.sjna.teamup.common.controller.constraint.UserIdConstraint;
import com.sjna.teamup.common.controller.constraint.UserNameConstraint;
import com.sjna.teamup.common.controller.constraint.UserPwConstraint;
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
