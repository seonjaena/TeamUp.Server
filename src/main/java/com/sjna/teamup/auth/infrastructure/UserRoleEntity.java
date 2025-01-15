package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.auth.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USER_ROLE")
public class UserRoleEntity {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "PRIORITY")
    private Integer priority;

    public static UserRoleEntity fromDomain(UserRole role) {
        UserRoleEntity roleEntity = new UserRoleEntity();
        roleEntity.idx = role.getIdx();
        roleEntity.name = role.getName();
        roleEntity.priority = role.getPriority();
        return roleEntity;
    }

    public UserRole toDomain() {
        return UserRole.builder()
                .idx(this.idx)
                .name(this.name)
                .priority(this.priority)
                .build();
    }

}
