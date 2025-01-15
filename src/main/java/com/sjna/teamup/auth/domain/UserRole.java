package com.sjna.teamup.auth.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRole {

    private Integer idx;
    private String name;
    private Integer priority;

}
