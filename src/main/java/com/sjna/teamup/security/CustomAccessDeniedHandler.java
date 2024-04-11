package com.sjna.teamup.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjna.teamup.dto.response.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * 스프링 시큐리티(필터)에서 HttpStatus 403 Forbidden이 발생한 경우 아래의 로직 실행
 * 참고: https://yoo-dev.tistory.com/28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 시스템적인 오류가 아니기 때문에 warning 로그로 출력한다. (에러 로그는 알람이 오며 알람이 너무 자주 오게되면 피로함을 느낌)
        log.warn("Not Authorized user requested. ip={}, uri={}, userId={}", request.getRemoteAddr(), request.getRequestURI(), request.getUserPrincipal().getName());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(
                        new ExceptionResponse("Unauthorized",
                                messageSource.getMessage("error.common.403",
                                        new String[]{},
                                        LocaleContextHolder.getLocale()))
                        )
                );
    }
}
