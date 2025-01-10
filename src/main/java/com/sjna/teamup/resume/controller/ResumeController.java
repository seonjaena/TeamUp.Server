package com.sjna.teamup.resume.controller;

import com.sjna.teamup.resume.controller.request.AddResumeRequest;
import com.sjna.teamup.resume.controller.response.ResumeResponse;
import com.sjna.teamup.resume.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping(value = "/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public void addResume(@Valid @RequestBody AddResumeRequest resumeRequest,
                          Principal principal) {
        resumeService.addResume(resumeRequest, principal.getName());
    }

    @GetMapping
    public ResumeResponse getResume(Principal principal) {
        return resumeService.getResume(principal.getName());
    }

}
