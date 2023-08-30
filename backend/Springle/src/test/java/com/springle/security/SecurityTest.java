package com.springle.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.springle.security.util.JwtProvider;
import com.springle.user.entity.Role;
import com.springle.user.entity.User;
import com.springle.util.AbstractContainerTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
public class SecurityTest extends AbstractContainerTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @DisplayName("JWT 생성된다")
    @Test
    void JWT_token_generated() throws Exception {

        User user = User.builder()
                        .loginId("loginId")
                        .loginPassword("1234")
                        .role(Role.USER)
                        .build();

        String token = jwtProvider.createToken(user.getId(), user.getLoginId(), user.getRole(), secretKey);

        assertThat(token).satisfies(t -> {
            assertThat(t).isNotNull();
            assertThat(t).isNotBlank();
            assertThat(t).isNotEmpty();
        });
    }

    @DisplayName("JWT로 payload 조회된다")
    @Test
    void getPayloadByJWT() throws Exception {

        User user = User.builder()
                        .loginId("loginId")
                        .loginPassword("1234")
                        .role(Role.USER)
                        .build();

        String token = jwtProvider.createToken(user.getId(), user.getLoginId(), user.getRole(), secretKey);

        long id = jwtProvider.getId(token, secretKey);
        String loginId = jwtProvider.getLoginId(token, secretKey);

        assertThat(user).satisfies(u -> {
            assertThat(u.getId()).isEqualTo(id);
            assertThat(u.getLoginId()).isEqualTo(loginId);
        });
    }

    @DisplayName("만료된 JWT 판별된다")
    @Test
    void expiredTokenDetected() throws Exception {

        final String expiredToken = Jwts.builder()
                                        .signWith(Keys.hmacShaKeyFor(
                                                secretKey.getBytes(StandardCharsets.UTF_8)),
                                            SignatureAlgorithm.HS256)
                                        .setExpiration(new Date(new Date().getTime() - 1))
                                        .compact();

        assertThat(jwtProvider.isExpired(expiredToken, secretKey)).isEqualTo(true);
    }

    @jakarta.transaction.Transactional
    @DisplayName("REFRESH TOKEN 생성된다")
    @Test
    void refreshTokenGenerated() throws Exception {

        User user = User.builder()
                        .loginId("loginId")
                        .loginPassword("1234")
                        .role(Role.USER)
                        .build();

        String refreshToken = jwtProvider.createRefreshToken("loginId", secretKey);

        assertThat(refreshToken).satisfies(t -> {
            assertThat(t).isNotNull();
            assertThat(t).isNotBlank();
        });
    }

    @Transactional
    @DisplayName("ReFresh Token Redis에 저장된다")
    @Test
    void refreshTokenIsStoredInRedis() throws Exception {

        User user = User.builder()
                        .loginId("loginId")
                        .loginPassword("1234")
                        .role(Role.USER)
                        .build();

        String refreshToken = jwtProvider.createRefreshToken("loginId", secretKey);

        boolean tokenExists = Boolean.TRUE.equals(redisTemplate.hasKey("loginId"));

        assertThat(tokenExists).isTrue();

    }

    @Transactional
    @DisplayName("Token Refersh된다")
    @Test
    void tokenIsRefreshed() throws Exception {

        User user = User.builder()
                        .loginId("loginId")
                        .loginPassword("1234")
                        .role(Role.USER)
                        .build();

        String refreshToken = jwtProvider.createRefreshToken("loginId", secretKey);

        String accessToken = jwtProvider.refreshAccessToken(refreshToken, secretKey);

        assertThat(accessToken).isNotNull();
    }

}
