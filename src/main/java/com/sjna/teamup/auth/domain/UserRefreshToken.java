package com.sjna.teamup.auth.domain;

import com.sjna.teamup.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRefreshToken {

    private Long idx;
    private String idxHash;
    private User user;
    private String value;

    public static UserRefreshToken from(String idxHash, User user, String value) {
        return UserRefreshToken.builder()
                .idxHash(idxHash)
                .user(user)
                .value(value)
                .build();
    }

    public void changeIdxHash(String idxHash) {
        this.idxHash = idxHash;
    }

}
