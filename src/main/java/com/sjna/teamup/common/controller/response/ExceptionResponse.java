package com.sjna.teamup.common.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {

    private String type;
    private String message;

}
