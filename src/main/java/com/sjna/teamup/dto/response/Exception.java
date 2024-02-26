package com.sjna.teamup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Exception {

    private int code;
    private String commonReason;
    private String customReason;

}
