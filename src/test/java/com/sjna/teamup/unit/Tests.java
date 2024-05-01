package com.sjna.teamup.unit;

import com.sjna.teamup.entity.User;
import com.sjna.teamup.entity.UserRole;
import com.sjna.teamup.entity.enums.USER_STATUS;
import com.sjna.teamup.repository.UserRepository;
import com.sjna.teamup.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class Tests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    void a() {
        UserRole role = UserRole.builder()
                .name("test")
                .priority(1)
                .build();

        User user = User.builder()
                .accountId("test-user")
                .accountPw("test-pw")
                .nickname("test-nickname")
                .role(role)
                .status(USER_STATUS.NORMAL)
                .name("test")
                .birth(LocalDate.now())
                .phone("010-1234-4321")
                .build();


        userRoleRepository.saveAndFlush(role);
        userRepository.saveAndFlush(user);

        List<UserRole> userRoles = userRoleRepository.findAll();
        userRoles.forEach(ur -> System.out.println("userRole: " + ur.getName()));

        List<User> users = userRepository.findAll();
        users.forEach(u -> System.out.println("user: " + u.getAccountId()));
    }
}
