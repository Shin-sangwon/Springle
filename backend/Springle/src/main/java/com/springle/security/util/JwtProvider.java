package com.springle.security.util;

import com.springle.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private final long accessTokenExpireTimeMs = 3600000L; // 1시간
    private final long refreshTokenExpireTimeMs = 1209600000L; // 2주일

    public Long getId(String token, String secretKey) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .get("id", Long.class);
    }

    public String getLoginId(String token, String secretKey) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .get("loginId", String.class);
    }

    public boolean isExpired(String token, String secretKey) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .require("exp", new Predicate<Date>() {
                    @Override
                    public boolean test(Date expirationDate) {
                        return expirationDate.before(new Date());
                    }
                })
                .build()
                .parseClaimsJws(token);
            return false;
        } catch (JwtException e) {
            return true;
        }
    }

    public String createToken(Long id, String loginId, Role role, String secretKey) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("loginId", loginId);
        claims.put("role", role.name());

        Date now = new Date();
        Date accessTokenExpiration = new Date(now.getTime() + accessTokenExpireTimeMs);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(accessTokenExpiration)
                   .signWith(
                       Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                       SignatureAlgorithm.HS256)
                   .compact();

    }

    public String createRefreshToken(String loginId, String secretKey) {

        Claims claims = Jwts.claims();
        claims.put("loginId", loginId);

        Date now = new Date();
        Date refreshTokenExpiration = new Date(now.getTime() + refreshTokenExpireTimeMs);

        String refreshToken = Jwts.builder()
                                  .setClaims(claims)
                                  .setIssuedAt(now)
                                  .setExpiration(refreshTokenExpiration)
                                  .signWith(Keys.hmacShaKeyFor(
                                          secretKey.getBytes(StandardCharsets.UTF_8)),
                                      SignatureAlgorithm.HS256)
                                  .compact();
        redisTemplate.opsForValue()
                     .set(loginId, refreshToken, refreshTokenExpireTimeMs, TimeUnit.MILLISECONDS);

        return refreshToken;

    }


}
