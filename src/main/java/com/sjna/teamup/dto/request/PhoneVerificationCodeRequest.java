package com.sjna.teamup.dto.request;

import com.sjna.teamup.validator.constraint.PhoneConstraint;
import lombok.Data;

@Data
public class PhoneVerificationCodeRequest {

    @PhoneConstraint
    private String phone;
    private String verificationCode;

}
