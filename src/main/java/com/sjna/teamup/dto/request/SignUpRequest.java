package com.sjna.teamup.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SignUpRequest {

    @Size(min = 2, max = 20, message = "이름은 2 ~ 20자 까지 가능합니다.")
    @Pattern(regexp = "[가-힣]{2,20}", message = "이름은 한글만 입력 가능합니다.")
    private String name;

    @Past
    private LocalDate birth;

    @Pattern(regexp = "(010|011)-\\d{3,4}-\\d{4}", message = "맞지 않는 휴대폰 번호 양식입니다.")
    private String phone;

    @Email(message = "맞지 않는 이메일 양식입니다.")
    private String email;

    @Size(min = 5, max = 20, message = "아이디는 5 ~ 20 글자만 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z90-9\\-_]{5,20}$", message = "아이디는 영문 대소문자, 숫자, 하이픈, 언더스코어만 사용 가능합니다.")
    private String userId;

    @Size(min = 8, max = 30, message = "비밀번호는 8 ~ 30 글자만 입력 가능합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.~_-])[A-Za-z\\d@$!%*?&#.~_-]{8,30}$", message = "비밀번호는 하나 이상의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String userPw;

    @Size(min = 8, max = 30, message = "비밀번호는 8 ~ 30 글자만 입력 가능합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.~_-])[A-Za-z\\d@$!%*?&#.~_-]{8,30}$", message = "비밀번호는 하나 이상의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String userPw2;

    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10 글자만 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "닉네임은 영문 대소문자, 숫자, 한글만 사용 가능합니다.")
    private String userNickname;

}
