package com.sjna.teamup.auth.infrastructure;

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

}
