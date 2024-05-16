package com.sjna.teamup.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TestResponse {

    private String profileImageUrl;
    private String nickname;
    private LocalDate birth;
    private String phone;

}
