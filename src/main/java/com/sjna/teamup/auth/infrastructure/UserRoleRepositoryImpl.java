package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.auth.domain.UserRole;
import com.sjna.teamup.auth.service.port.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleJpaRepository userRoleJpaRepository;

    @Override
    public List<UserRole> findAll(Sort sort) {
        return userRoleJpaRepository.findAll(sort).stream().map(UserRoleEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public UserRole save(UserRole userRole) {
        return userRoleJpaRepository.save(UserRoleEntity.fromDomain(userRole)).toDomain();
    }
}
