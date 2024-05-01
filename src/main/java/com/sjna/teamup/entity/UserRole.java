package com.sjna.teamup.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "USER_ROLE")
public class UserRole {

    @Id
    @Column(name = "IDX")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Builder
    public UserRole(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

}
