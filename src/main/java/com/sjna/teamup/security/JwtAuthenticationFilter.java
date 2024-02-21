package com.sjna.teamup.security;

import com.sjna.teamup.exception.JwtExpirationException;
import com.sjna.teamup.exception.UnAuthenticatedException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = jwtProvider.parseToken(request);

        if(StringUtils.hasText(token) && !jwtProvider.isTokenExpired(token)) {
            Authentication authentication = jwtProvider.getAuthUserInfo(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
//        else if(!StringUtils.hasText(token)) {
//            throw new UnAuthenticatedException("JWT is not exist. ip=" + request.getRemoteAddr());
//        }else if(!isTokenExpired) {
//            throw new JwtExpirationException("JWT is expired. userId=" + jwtProvider.getUserId(token));
//        }

        chain.doFilter(request, response);
    }
}
