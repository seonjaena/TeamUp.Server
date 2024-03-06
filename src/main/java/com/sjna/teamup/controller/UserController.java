package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.dto.request.VerificationCodeRequest;
import com.sjna.teamup.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public String test() {
        return "hello";
    }

    @GetMapping(value = "/available/userId/{userId}")
    public boolean checkUserIdAvailable(
            @Pattern(regexp = "^[a-zA-Z90-9\\-_]{5,20}$", message = "constraint.user-id.pattern")
            @PathVariable(name = "userId") String userId) {
        return userService.checkUserIdAvailable(userId);
    }

    @GetMapping(value = "/available/userNickname/{userNickname}")
    public boolean checkUserNicknameAvailable(
            @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "constraint.user-nickname.pattern")
            @PathVariable(name = "userNickname") String userNickname) {
        return userService.checkUserNicknameAvailable(userNickname);
    }

    @PostMapping(value = "/verification-code")
    public void sendEmailVerificationCode(@RequestBody VerificationCodeRequest verificationCodeRequest) {
        userService.saveVerificationCode(verificationCodeRequest);
    }

    @PostMapping
    public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
    }

}
