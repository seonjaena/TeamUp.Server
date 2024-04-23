package com.sjna.teamup.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SignUpRequest {

    @Pattern(regexp = "[가-힣]{2,20}", message = "constraint.user-name.pattern")
    private String name;

    @Email(message = "constraint.user-email.pattern")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.~_-])[A-Za-z\\d@$!%*?&#.~_-]{8,30}$", message = "constraint.user-pw.pattern")
    private String userPw;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.~_-])[A-Za-z\\d@$!%*?&#.~_-]{8,30}$", message = "constraint.user-pw.pattern")
    private String userPw2;

}
