package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.auth.domain.UserRefreshToken;
import com.sjna.teamup.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "USER_REFRESH_TOKEN")
public class UserRefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long idx;

    @Column(name = "IDX_HASH", columnDefinition = "CHAR(32)")
    private String idxHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))     // RDBMS에서 외래키를 설정하기 위해서 사용 (NO_CONSTRAINT: 논리적으로만 연관관계 매핑)
    private UserEntity user;

    @Column(name = "TOKEN_VALUE", length = 300)
    private String value;

    public static UserRefreshTokenEntity fromDomain(UserRefreshToken refreshToken) {
        UserRefreshTokenEntity refreshTokenEntity = new UserRefreshTokenEntity();
        refreshTokenEntity.idx = refreshToken.getIdx();
        refreshTokenEntity.idxHash = refreshToken.getIdxHash();
        refreshTokenEntity.user = UserEntity.fromDomain(refreshToken.getUser());
        refreshTokenEntity.value = refreshToken.getValue();
        return refreshTokenEntity;
    }

    public UserRefreshToken toDomain() {
        return UserRefreshToken.builder()
                .idx(this.idx)
                .idxHash(this.idxHash)
                .user(user.toDomain())
                .value(this.value)
                .build();
    }

}
