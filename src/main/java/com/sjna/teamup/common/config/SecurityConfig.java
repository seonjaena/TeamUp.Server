package com.sjna.teamup.common.config;

import com.sjna.teamup.auth.controller.port.UserRoleService;
import com.sjna.teamup.common.filter.JwtAuthenticationFilter;
import com.sjna.teamup.common.security.CustomAccessDeniedHandler;
import com.sjna.teamup.common.security.CustomAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private static final Map<HttpMethod, String[]> PERMIT_ALL_METHOD_URL = Map.ofEntries(
            Map.entry(HttpMethod.GET, new String[]{"/auth/renewal", "/user/available/**", "/common/health-check", "/user/link/password/*"}),
            Map.entry(HttpMethod.POST, new String[]{"/user", "/auth", "/auth/email-verification-code"}),
            Map.entry(HttpMethod.PUT, new String[]{}),
            Map.entry(HttpMethod.PATCH, new String[]{"/auth/email-verification", "/user/password"}),
            Map.entry(HttpMethod.DELETE, new String[]{})
    );

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final UserRoleService userRoleService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors) ->
                        cors.configurationSource(corsConfigurationSource())
                )
                .csrf((csrf) ->
                        csrf.disable()
                )
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.GET, "/user").hasAuthority("PRIVATE_BRONZE")
                                .requestMatchers(HttpMethod.GET, PERMIT_ALL_METHOD_URL.get(HttpMethod.GET)).permitAll()
                                .requestMatchers(HttpMethod.POST, PERMIT_ALL_METHOD_URL.get(HttpMethod.POST)).permitAll()
                                .requestMatchers(HttpMethod.PUT, PERMIT_ALL_METHOD_URL.get(HttpMethod.PUT)).permitAll()
                                .requestMatchers(HttpMethod.PATCH, PERMIT_ALL_METHOD_URL.get(HttpMethod.PATCH)).permitAll()
                                .requestMatchers(HttpMethod.DELETE, PERMIT_ALL_METHOD_URL.get(HttpMethod.DELETE)).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin((formLogin) ->
                        formLogin.disable()
                )
                .httpBasic((httpBasic) ->
                        httpBasic.disable()
                )
                .exceptionHandling((exceptionHandler) ->
                        exceptionHandler
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                ;

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(userRoleService.getRoleHierarchy());
        return hierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static AuthorizationManager<RequestAuthorizationContext> hasIpAddress(String[] ipAddress) {
        return (authentication, object) -> {
            HttpServletRequest request = object.getRequest();
            boolean isAccessible = Arrays.stream(ipAddress)
                    .anyMatch(ip -> new IpAddressMatcher(ip).matches(request));
            return new AuthorizationDecision(isAccessible);
        };
    }

}
