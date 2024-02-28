package com.sjna.teamup.service;

import com.sjna.teamup.entity.UserRole;
import com.sjna.teamup.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public String getRoleHierarchy() {
        List<UserRole> roleHierarchies = userRoleRepository.findAll(Sort.by(Sort.Direction.ASC, "priority"));

        StringBuilder roleStr = new StringBuilder();

        roleStr.append(roleHierarchies.get(0).getName());

        for(int i = 1; i < roleHierarchies.size(); i++) {
            roleStr.append(" > ");
            roleStr.append(roleHierarchies.get(i).getName());
        }

        return roleStr.toString();
    }

}
