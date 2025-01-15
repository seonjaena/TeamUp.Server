package com.sjna.teamup.auth.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, Integer> {

}
