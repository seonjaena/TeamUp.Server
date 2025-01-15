package com.sjna.teamup.auth.service.port;

import com.sjna.teamup.auth.domain.UserRole;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface UserRoleRepository {

    List<UserRole> findAll(Sort sort);
    UserRole save(UserRole userRole);

}
