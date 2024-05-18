package com.sjna.teamup.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneVerificationCodeRequest {

    @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$", message = "constraint.user-phone.pattern")
    private String phone;
    private String verificationCode;

}
