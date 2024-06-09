package com.sjna.teamup.dto.request;

import com.sjna.teamup.validator.constraint.UserIdConstraint;
import lombok.Data;

@Data
public class EmailVerificationCodeRequest {

    @UserIdConstraint
    private String email;
    private String verificationCode;

}
