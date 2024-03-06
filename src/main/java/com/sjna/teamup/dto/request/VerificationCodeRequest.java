package com.sjna.teamup.dto.request;

import com.sjna.teamup.entity.enums.VERIFICATION_CODE_TYPE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerificationCodeRequest {

    @Email(message = "constraint.user-email.pattern")
    private String email;

    @Pattern(regexp = "(010|011)-\\d{3,4}-\\d{4}", message = "constraint.user-phone.pattern")
    private String phone;

    private VERIFICATION_CODE_TYPE verificationCodeType;



}
