package com.sjna.teamup.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SignUpRequest {

    @Pattern(regexp = "[가-힣]{2,20}", message = "constraint.user-name.pattern")
    private String name;

    @Past
    private LocalDate birth;

    @Pattern(regexp = "(010|011)-\\d{3,4}-\\d{4}", message = "constraint.user-phone.pattern")
    private String phone;

    @Email(message = "constraint.user-email.pattern")
    private String email;

    @Pattern(regexp = "^[a-zA-Z90-9\\-_]{5,20}$", message = "constraint.user-id.pattern")
    private String userId;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.~_-])[A-Za-z\\d@$!%*?&#.~_-]{8,30}$", message = "constraint.user-pw.pattern")
    private String userPw;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.~_-])[A-Za-z\\d@$!%*?&#.~_-]{8,30}$", message = "constraint.user-pw.pattern")
    private String userPw2;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "constraint.user-nickname.pattern")
    private String userNickname;

}
