package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.service.UserService;
import jakarta.validation.Valid;
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

    @GetMapping(value = "/userId/available/{userId}")
    public void checkUserIdAvailable(@PathVariable(name = "userId") String userId) {
    }

    @PostMapping
    public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
    }

}
