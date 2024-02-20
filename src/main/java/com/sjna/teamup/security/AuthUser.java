package com.sjna.teamup.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AuthUser extends User {

    private final com.sjna.teamup.entity.User user;

    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, com.sjna.teamup.entity.User user) {
        super(username, password, authorities);
        this.user = user;
    }
}
