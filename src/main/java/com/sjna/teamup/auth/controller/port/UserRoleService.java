package com.sjna.teamup.auth.controller.port;

import com.sjna.teamup.auth.domain.UserRole;

public interface UserRoleService {

    UserRole getBasic();
    String getRoleHierarchy();

}
