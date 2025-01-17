package com.sjna.teamup.resume.controller.port;

import com.sjna.teamup.resume.controller.request.AddResumeRequest;
import com.sjna.teamup.resume.controller.response.ResumeResponse;

public interface ResumeService {

    void addResume(AddResumeRequest resumeRequest, String userId);
    ResumeResponse getResume(String userId);

}
