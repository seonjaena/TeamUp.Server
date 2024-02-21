package com.sjna.teamup.security;

import com.sjna.teamup.dto.JwtDto;
import com.sjna.teamup.exception.UnAuthenticatedException;
import com.sjna.teamup.exception.UnAuthorizedException;
import com.sjna.teamup.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    private final Key key;
    private final Header header;
    private final UserService userService;
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String BEARER_PREFIX = "Bearer-";
    private final static String ROLES = "roles";

    @Value("${jwt.expire.access}")
    private Long accessTokenExpireMilliSec;

    @Value("${jwt.expire.refresh}")
    private Long refreshTokenExpireMilliSec;

    public JwtProvider(@Value("${jwt.secret}") String secretKey, UserService userService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        Jwts.HeaderBuilder header = Jwts.header();
        header.setType(Header.TYPE);
        header.setType(Header.JWT_TYPE);
        this.header = header.build();
        this.userService = userService;
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

    /**
     * JWT를 사용하여 사용자에 대해서 검증 (Authentication, Authorization)
     * @param token
     * @return
     */
    public Authentication getAuthUserInfo(String token) {
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();

        // token에 userId 정보가 있는지 확인
        if(!StringUtils.hasText(userId)) {
            throw new UnAuthenticatedException("User Id is not exist in token.");
        }

        // token에 사용자 권한에 대한 정보가 있는지 확인
        if(claims.get(ROLES) == null) {
            throw new UnAuthorizedException("User is not authorized. userId=" + userId);
        }


        UserDetails authUser = userService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities());
    }

    /**
     * 사용자의 요청의 헤더에서 JWT를 추출
     * @param request
     * @return
     */
    public String parseToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }else {
            throw new UnAuthenticatedException("User is not authenticated. ip=" + request.getRemoteAddr());
        }
    }

    /**
     * 사용자의 JWT의 만료 여부를 반환
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().before(new Date());
    }

    /**
     * JWT에서 사용자 ID를 추출
     * @param token
     * @return
     */
    public String getUserId(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    private String createAccessToken(String userId, List<String> roles, Date now) {
        return Jwts.builder()
                .setHeader(header)
                .subject(userId)
                .claim(ROLES, roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpireMilliSec))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createRefreshToken(String userId, List<String> roles, Date now) {
        return Jwts.builder()
                .setHeader(header)
                .subject(userId)
                .claim(ROLES, roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpireMilliSec))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims parseClaims(String token) {
        if(!StringUtils.hasText(token)) {
            throw new UnAuthenticatedException("token is not exist.");
        }
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
