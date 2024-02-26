package com.sjna.teamup.exception.handler;

import com.sjna.teamup.dto.response.Exception;
import com.sjna.teamup.exception.UnAuthenticatedException;
import com.sjna.teamup.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonRestExceptionHandler {

    @ExceptionHandler(value = UnAuthenticatedException.class)
    public ResponseEntity unAuthenticatedException(UnAuthenticatedException e) {
        /**
         * TODO: 401 에러 코드는 인증에 대한 에러지만 code는 UNAUTHORIZED. 이 이유를 확인해보고 만약 내가 틀리게 사용하고 있다면 해결해야 함.
         * TODO: 401 에러 타입은 Unauthorized. 하지만 401은 인증에 대한 내용을 담고 싶기 때문에 임의로 타입 변경. 이게 옳은지 확인.
         */
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new Exception(HttpStatus.UNAUTHORIZED.value(), "Unauthenticated", e.getMessage()));
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResponseEntity unAuthorizedException(UnAuthorizedException e) {
        /**
         * TODO: 403의 에러 메시지는 Forbidden. 하지만 403은 인가에 대한 내용을 담고 싶기 때문에 임의로 타입 변경. 이게 옳은지 확인. 또한 FORBIDDEN 의미 조사 필요.
         */
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Exception(HttpStatus.FORBIDDEN.value(), "Unauthorized", e.getMessage()));
    }

    @ExceptionHandler(value = java.lang.Exception.class)
    public ResponseEntity unknownException(java.lang.Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Exception(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage()));
    }

}
