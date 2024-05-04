package com.sjna.teamup.dto.request;

import com.sjna.teamup.entity.enums.VERIFICATION_CODE_TYPE;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailVerificationCodeRequest {

    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "constraint.user-email.pattern")
    private String email;
    private String verificationCode;

}
