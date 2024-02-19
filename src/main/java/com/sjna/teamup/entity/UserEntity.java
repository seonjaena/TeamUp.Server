package com.sjna.teamup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "IDX")
    @GeneratedValue
    private Long idx;

    @Column(name = "ID", length = 100)
    private String id;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "NICKNAME", length = 100)
    private String nickname;

}
