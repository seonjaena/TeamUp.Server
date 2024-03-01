package com.sjna.teamup.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjna.teamup.dto.response.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * 스프링 시큐리티(필터)에서 HttpStatus 401 Unauthorized가 발생한 경우 아래의 로직 실행
 * Unauthorized지만 실제로는 Unauthenticated가 더 적절해 보임 따라서 응답에는 Unauthenticated 라고 적고 리턴
 *
 * 참고: https://yoo-dev.tistory.com/28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 시스템적인 오류가 아니기 때문에 warning 로그로 출력한다. (에러 로그는 알람이 오며 알람이 너무 자주 오게되면 피로함을 느낌)
        log.warn("Not Authenticated user requested. ip={}, uri={}", request.getRemoteAddr(), request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), "UnAuthenticated", authException.getMessage())));
    }
}
