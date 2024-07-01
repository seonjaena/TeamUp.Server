package com.sjna.teamup.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class AddResumeRequest {

    private String introduction;
    private String projectUrl;
    private String skill;
    private String experience;
    private String additionalInfo;
    private String certificate;
    private Map<String, Short> languages = new HashMap<>();

}
