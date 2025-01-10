package com.sjna.teamup.auth.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {

}
