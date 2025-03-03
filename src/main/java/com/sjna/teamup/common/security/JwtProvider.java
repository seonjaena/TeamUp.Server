package com.sjna.teamup.common.security;

import com.sjna.teamup.common.domain.Jwt;
import com.sjna.teamup.common.domain.exception.UnAuthenticatedException;
import com.sjna.teamup.common.domain.exception.UnAuthorizedException;
import com.sjna.teamup.common.service.port.LocaleHolder;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// TODO: AUTH로 옮기고 Interface를 구현하도록 수정 (infrastructure 레이어)
@Component
public class JwtProvider {

    private final Key key;
    private final Header header;
    private final MessageSource messageSource;
    private final LocaleHolder localeHolder;
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String BEARER_PREFIX = "Bearer";
    private final static String ROLES = "roles";

    @Value("${jwt.expire.access}")
    private Long accessTokenExpireMilliSec;

    @Value("${jwt.expire.refresh}")
    private Long refreshTokenExpireMilliSec;

    public JwtProvider(@Value("${jwt.secret}") String secretKey, MessageSource messageSource, LocaleHolder localeHolder) {
        this.messageSource = messageSource;
        this.localeHolder = localeHolder;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        Jwts.HeaderBuilder header = Jwts.header();
        header.setType("JWT");
        this.header = header.build();
    }

    /**
     * AccessToken과 RefreshToken을 생성하는 메서드
     * @param userId
     * @param roles
     * @return
     */
    public Jwt createToken(String userId, List<String> roles) {
        Date now = new Date();

        String accessToken = createAccessToken(userId, roles, now);
        String refreshToken = createRefreshToken(userId, roles, now);

        return new Jwt(accessToken, refreshToken);
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
    public void validateToken(String token) {
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();

        // token에 userId 정보가 있는지 확인 & 만료되지 않았는지 확인
        if(!StringUtils.hasText(userId) || claims.getExpiration().before(new Date())) {
            throw new UnAuthenticatedException(
                    messageSource.getMessage("notice.re-login.request", null, localeHolder.getLocale())
            );
        }

        // token에 사용자 권한에 대한 정보가 있는지 확인
        if(claims.get(ROLES) == null) {
            throw new UnAuthorizedException(
                    messageSource.getMessage("notice.re-login.request", null, localeHolder.getLocale())
            );
        }
    }

    /**
     * 사용자의 요청의 헤더에서 JWT를 추출
     * null도 리턴하기 때문에 Optional로 감싼다.
     * @param request
     * @return
     */
    public Optional<String> parseToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return Optional.of(authorization.substring(7));
        }
        return Optional.ofNullable(null);
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

    public List<String> getUserRoles(String token) {
        Claims claims = parseClaims(token);
        return claims.get(ROLES, List.class);
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

    public Claims parseClaims(String token) {
        if(!StringUtils.hasText(token)) {
            throw new UnAuthenticatedException(
                    messageSource.getMessage("notice.re-login.request", null, localeHolder.getLocale())
            );
        }
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
