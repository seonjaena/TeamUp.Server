package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.LoginRequest;
import com.sjna.teamup.dto.response.LoginResponse;
import com.sjna.teamup.dto.response.RefreshAccessTokenResponse;
import com.sjna.teamup.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;

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

}
