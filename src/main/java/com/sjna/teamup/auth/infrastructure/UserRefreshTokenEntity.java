package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
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

    public static UserRefreshTokenEntity from(String idxHash, UserEntity user, String value) {
        UserRefreshTokenEntity token = new UserRefreshTokenEntity();
        token.setIdxHash(idxHash);
        token.setUser(user);
        token.setValue(value);
        return token;
    }

    public void changeIdxHash(String idxHash) {
        this.idxHash = idxHash;
    }

}
