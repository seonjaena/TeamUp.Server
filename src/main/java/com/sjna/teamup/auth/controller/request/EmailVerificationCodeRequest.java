package com.sjna.teamup.auth.controller.request;

import com.sjna.teamup.common.controller.constraint.UserIdConstraint;
import lombok.Data;

@Data
public class EmailVerificationCodeRequest {

    @UserIdConstraint
    private String email;
    private String verificationCode;

}
