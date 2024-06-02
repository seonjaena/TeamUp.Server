package com.sjna.teamup.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginChangePasswordRequest {

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "constraint.user-pw.pattern")
    private String oldPw;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "constraint.user-pw.pattern")
    private String userPw;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$", message = "constraint.user-pw.pattern")
    private String userPw2;

}
