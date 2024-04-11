package com.sjna.teamup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshAccessTokenResponse {

    private String accessToken;
    private String refreshTokenIdxHash;

}
