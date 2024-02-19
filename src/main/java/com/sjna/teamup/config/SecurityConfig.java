package com.sjna.teamup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) ->
                        csrf.disable()
                )
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/api/user", "/api/user/*").hasRole("FREE")
                                .anyRequest().authenticated()
                )
                .formLogin((formLogin) ->
                        formLogin.disable()
                )
                .httpBasic((httpBasic) ->
                        httpBasic.disable()
                )
                ;

        return http.build();
    }
}
