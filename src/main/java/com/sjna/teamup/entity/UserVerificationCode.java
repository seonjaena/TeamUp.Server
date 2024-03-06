package com.sjna.teamup.entity;

import com.sjna.teamup.entity.enums.VERIFICATION_CODE_TYPE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_VERIFICATION_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserVerificationCode {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "PHONE_NUMBER")
    private String phone;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @Column(name = "VERIFICATION_CODE")
    private String verificationCode;

    @Column(name = "TYPE")
    @Convert(converter = VERIFICATION_CODE_TYPE.Converter.class)
    private VERIFICATION_CODE_TYPE type;

    @Builder
    public UserVerificationCode(String phone, String email, String verificationCode, VERIFICATION_CODE_TYPE type) {
        this.phone = phone;
        this.email = email;
        this.verificationCode = verificationCode;
        this.type = type;
    }

}
