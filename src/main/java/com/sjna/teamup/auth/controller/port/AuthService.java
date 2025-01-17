package com.sjna.teamup.auth.controller.port;

import com.sjna.teamup.auth.controller.request.EmailVerificationCodeRequest;
import com.sjna.teamup.auth.controller.request.LoginRequest;
import com.sjna.teamup.auth.controller.request.PhoneVerificationCodeRequest;
import com.sjna.teamup.auth.controller.response.LoginResponse;
import java.security.NoSuchAlgorithmException;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest) throws NoSuchAlgorithmException;
    void sendVerificationCode(EmailVerificationCodeRequest verificationCodeRequest);
    void sendVerificationCode(PhoneVerificationCodeRequest verificationCodeRequest);
    void verifyEmailVerificationCode(EmailVerificationCodeRequest verificationCodeRequest);
    void verifyPhoneVerificationCode(String userId, PhoneVerificationCodeRequest verificationCodeRequest);

}
