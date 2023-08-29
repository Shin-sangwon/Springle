package com.springle.security.util;

import com.springle.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final long accessTokenExpireTimeMs = 3600000L; // 1시간
    private final long refreshTokenExpireTimeMs = 1209600000L; // 2주일

    public static long getId(String token, String secretKey) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .get("id", long.class);
    }

    public static String getLoginId(String token, String secretKey) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .get("loginId", String.class);
    }

    public boolean isExpired(String token, String secretKey) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getExpiration()
                   .before(new Date());
    }

    public String createToken(Long id, String loginId, Role role, String secretKey) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("loginId", loginId);
        claims.put("role", role.name());

        Date now = new Date();
        Date accessTokenExpiration = new Date(now.getTime() + accessTokenExpireTimeMs);
        Date refreshTokenExpiration = new Date(now.getTime() + refreshTokenExpireTimeMs);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(accessTokenExpiration)
                   .signWith(
                       Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                       SignatureAlgorithm.HS256)
                   .compact();

    }


}
