package com.sjna.teamup.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Jwt {

    private String accessToken;
    private String refreshToken;

}
