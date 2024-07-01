package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.AddResumeRequest;
import com.sjna.teamup.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public void addResume(@RequestBody AddResumeRequest resumeRequest,
                          Principal principal) {
        resumeService.addResume(resumeRequest, principal.getName());
    }

}
