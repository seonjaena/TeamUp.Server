package com.sjna.teamup.auth.controller.request;

import com.sjna.teamup.common.controller.constraint.PhoneConstraint;
import lombok.Data;

@Data
public class PhoneVerificationCodeRequest {

    @PhoneConstraint
    private String phone;
    private String verificationCode;

}
