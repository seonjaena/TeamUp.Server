package com.sjna.teamup.common.security;

import com.sjna.teamup.user.infrastructure.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AuthUser extends User {

    private final UserEntity user;

    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, UserEntity user) {
        super(username, password, authorities);
        this.user = user;
    }
}
