package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.LoginRequest;
import com.sjna.teamup.dto.request.EmailVerificationCodeRequest;
import com.sjna.teamup.dto.request.PhoneVerificationCodeRequest;
import com.sjna.teamup.dto.response.LoginResponse;
import com.sjna.teamup.dto.response.RefreshAccessTokenResponse;
import com.sjna.teamup.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequestDto) throws NoSuchAlgorithmException {
        return authService.login(loginRequestDto);
    }

    @GetMapping(value = "/renewal")
        public RefreshAccessTokenResponse refreshAccessToken(@RequestParam(name = "refreshTokenIdxHash") String refreshTokenIdxHash) throws NoSuchAlgorithmException {
        return authService.refreshAccessToken(refreshTokenIdxHash);
    }

    @PostMapping(value = "/email-verification-code")
    public void sendEmailVerificationCode(@Valid @RequestBody EmailVerificationCodeRequest verificationCodeRequest) {
        authService.sendVerificationCode(verificationCodeRequest);
    }

    @PostMapping(value = "/phone-verification-code")
    public void sendPhoneVerificationCode(@Valid @RequestBody PhoneVerificationCodeRequest phoneVerificationCodeRequest) {
        authService.sendVerificationCode(phoneVerificationCodeRequest);
    }

    @PatchMapping(value = "/email-verification")
    public void verifyEmailCode(@Valid @RequestBody EmailVerificationCodeRequest verificationCodeRequest) {
        authService.verifyEmailVerificationCode(verificationCodeRequest);
    }

    @PatchMapping(value = "/phone-verification")
    public void verifyPhoneCode(@Valid @RequestBody PhoneVerificationCodeRequest verificationCodeRequest, Principal principal) {
        authService.verifyPhoneVerificationCode(principal.getName(), verificationCodeRequest);
    }

}
