package com.sjna.teamup.dto.request;

import com.sjna.teamup.validator.constraint.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class AddResumeRequest {

    @SizeConstraint(max = 1000, message = "error.introduction.max-size", params = {"1000"})
    private String introduction;

    @ListSizeConstraint(max = 5, message = "error.project-urls.max-size", params = {"5"})
    @ListElementSizeConstraint(max = 200, message = "error.project-url.max-size", params = {"200"})
    private List<String> projectUrls = new ArrayList<>();

    @SizeConstraint(max = 1000, message = "error.skill.max-size", params = {"1000"})
    private String skill;

    @SizeConstraint(max = 1000, message = "error.experience.max-size", params = {"1000"})
    private String experience;

    @SizeConstraint(max = 1000, message = "error.additional-info.max-size", params = {"1000"})
    private String additionalInfo;

    @ListSizeConstraint(max = 10, message = "error.certificates.max-size", params = {"10"})
    @ListElementSizeConstraint(max = 100, message = "error.certificate.max-size", params = {"100"})
    private List<String> certificates = new ArrayList<>();

    @MapSizeConstraint(max = 10, message = "error.language-grade.pattern", params = {"10"})
    @MapElementPatternConstraint(regexp = "^[1-5]$", message = "error.language-grade.pattern", params = {"1", "5"})
    private Map<String, String> languages = new HashMap<>();

}
