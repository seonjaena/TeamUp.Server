package com.sjna.teamup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshTokenIdxHash;

}
