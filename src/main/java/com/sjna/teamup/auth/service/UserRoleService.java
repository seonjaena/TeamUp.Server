package com.sjna.teamup.auth.service;

import com.sjna.teamup.auth.infrastructure.UserRoleEntity;
import com.sjna.teamup.auth.infrastructure.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final UserRoleRepository userRoleRepository;

    public String getRoleHierarchy() {
        // local 프로필인 경우 In Memory DB를 사용하기 때문에 USER_ROLE에 기본 데이터를 넣어준다.
        initLocalDbData();

        List<UserRoleEntity> roleHierarchies = userRoleRepository.findAll(Sort.by(Sort.Direction.ASC, "priority"));

        StringBuilder roleStr = new StringBuilder();

        roleStr.append(roleHierarchies.get(0).getName());

        for(int i = 1; i < roleHierarchies.size(); i++) {
            roleStr.append(" > ");
            roleStr.append(roleHierarchies.get(i).getName());
        }

        return roleStr.toString();
    }

    private void initLocalDbData() {
        if(activeProfile.equals("local")) {
            List<String> roleNames = Arrays.asList("ADMIN", "PRIVATE_GOLD", "PRIVATE_SILVER", "PRIVATE_BRONZE");

            for(int i = 1; i <= roleNames.size(); i++) {
                userRoleRepository.save(UserRoleEntity.builder()
                        .name(roleNames.get(i - 1))
                        .priority(i)
                        .build());
            }
        }
    }

}
