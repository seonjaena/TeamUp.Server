package com.sjna.teamup.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/common")
@RequiredArgsConstructor
public class CommonController {

    @GetMapping(value = "/health-check")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok().build();
    }

}
