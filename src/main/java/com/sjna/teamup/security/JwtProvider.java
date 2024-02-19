package com.sjna.teamup.security;

import com.sjna.teamup.dto.JwtDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    private final Key key;
    private final Header header;

    @Value("${jwt.expire.access}")
    private Long accessTokenExpireMilliSec;

    @Value("${jwt.expire.refresh}")
    private Long refreshTokenExpireMilliSec;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        Jwts.HeaderBuilder header = Jwts.header();
        header.setType(Header.TYPE);
        header.setType(Header.JWT_TYPE);
        this.header = header.build();
    }

    /**
     * AccessToken과 RefreshToken을 생성하는 메서드
     * @param userId
     * @param roles
     * @return
     */
    public JwtDto createToken(String userId, List<String> roles) {
        Date now = new Date();

        String accessToken = createAccessToken(userId, roles, now);
        String refreshToken = createRefreshToken(userId, roles, now);

        return new JwtDto(accessToken, refreshToken);
    }

    /**
     * AccessToken을 갱신하는 메서드
     * @param userId
     * @param roles
     * @return
     */
    public String refreshAccessToken(String userId, List<String> roles) {
        return createAccessToken(userId, roles, new Date());
    }

    private String createAccessToken(String userId, List<String> roles, Date now) {
        return Jwts.builder()
                .setHeader(header)
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpireMilliSec))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createRefreshToken(String userId, List<String> roles, Date now) {
        return Jwts.builder()
                .setHeader(header)
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpireMilliSec))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

}
