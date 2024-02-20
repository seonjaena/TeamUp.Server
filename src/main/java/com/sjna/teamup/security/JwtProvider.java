package com.sjna.teamup.security;

import com.sjna.teamup.dto.JwtDto;
import com.sjna.teamup.exception.UnAuthenticatedException;
import com.sjna.teamup.service.UserDetailService;
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
    private final UserDetailService userDetailService;
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String BEARER_PREFIX = "Bearer ";
    private final static String ROLES = "roles";

    @Value("${jwt.expire.access}")
    private Long accessTokenExpireMilliSec;

    @Value("${jwt.expire.refresh}")
    private Long refreshTokenExpireMilliSec;

    public JwtProvider(@Value("${jwt.secret}") String secretKey, UserDetailService userDetailService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        Jwts.HeaderBuilder header = Jwts.header();
        header.setType(Header.TYPE);
        header.setType(Header.JWT_TYPE);
        this.header = header.build();
        this.userDetailService = userDetailService;
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

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        if(claims.get(ROLES) == null) {
            throw new UnAuthenticatedException("접근 권한이 없습니다.");
        }
        String subject = claims.getSubject();
        UserDetails authUser = userDetailService.loadUserByUsername(subject);
        return new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities());
    }

    public String parseToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(authorization) && StringUtils.hasText(BEARER_PREFIX) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }else {
            throw new UnAuthenticatedException("User is not authenticated. ip=" + request.getRemoteAddr());
        }
    }

    public boolean validateToken(String token) {
        if(parseClaims(token).getExpiration().before(new Date())) {
            throw new JwtException("JWT Token is expired");
        }
        return true;
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
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
