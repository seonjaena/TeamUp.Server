package com.sjna.teamup.common.filter;

import com.sjna.teamup.common.domain.exception.UnAuthenticatedException;
import com.sjna.teamup.common.domain.exception.UserIdNotFoundException;
import com.sjna.teamup.common.security.AuthUser;
import com.sjna.teamup.common.security.JwtProvider;
import com.sjna.teamup.common.service.port.LocaleHolder;
import com.sjna.teamup.user.controller.port.UserService;
import com.sjna.teamup.user.domain.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final LocaleHolder localeHolder;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final MessageSource messageSource;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = jwtProvider.parseToken(request)
                .orElse("");

        jwtProvider.validateToken(token);
        String userId = jwtProvider.getUserId(token);

        try {
            User user = userService.getNotDeletedUser(userId);
            UserDetails authUser = new AuthUser(user.getAccountId(), user.getAccountPw(), user.getAuthorities(), user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(UserIdNotFoundException e) {
            log.warn(e.getMessage());
            // 부정한 방법으로 접속하는 사용자에게 정보를 주지 않기 위해 다른 예외로 바꿔서 throw
            throw new UnAuthenticatedException(messageSource.getMessage("notice.re-login.request", null, localeHolder.getLocale()));
        }

        chain.doFilter(request, response);
    }
}
