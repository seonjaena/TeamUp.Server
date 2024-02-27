package com.sjna.teamup.service;

import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.enums.USER_STATUS;
import com.sjna.teamup.repository.UserRepository;
import com.sjna.teamup.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User getUser(String userId) {
        return userRepository.findByIdAndStatus(userId, USER_STATUS.NORMAL).
                orElseThrow(() -> new UsernameNotFoundException("Can't find userId. userId=" + userId));
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = getUser(userId);

        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return new AuthUser(user.getId(), user.getPw(), roles, user);
    }

}
