package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.LoginDto;
import com.sjna.teamup.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class AuhController {

    private final AuthService authService;

    @PostMapping
    public void login(@RequestBody LoginDto loginDto) {

    }

}
