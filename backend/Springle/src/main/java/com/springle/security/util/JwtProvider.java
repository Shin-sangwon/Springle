package com.springle.security.util;

import com.springle.member.entity.Role;
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

    public String refreshAccessToken(String refreshToken, String secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                                .build()
                                .parseClaimsJws(refreshToken)
                                .getBody();

            String loginId = claims.get("loginId", String.class);
            // Redis 또는 데이터베이스에서 저장된 refreshToken과 전달된 refreshToken이 일치하는지 확인
            String storedRefreshToken = redisTemplate.opsForValue()
                                                     .get(loginId);

            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new RuntimeException();
            }

            // 저장된 refreshToken이 유효하면, 새로운 Access Token을 발급
            Date now = new Date();
            Date accessTokenExpiration = new Date(now.getTime() + accessTokenExpireTimeMs);

            Claims newClaims = Jwts.claims();
            newClaims.put("loginId", loginId);

            return Jwts.builder()
                       .setClaims(newClaims)
                       .setIssuedAt(now)
                       .setExpiration(accessTokenExpiration)
                       .signWith(Keys.hmacShaKeyFor(
                               secretKey.getBytes(StandardCharsets.UTF_8)),
                           SignatureAlgorithm.HS256)
                       .compact();

        } catch (Exception e) {
            // Refresh Token 검증 실패
            throw new RuntimeException();
        }
    }


}
