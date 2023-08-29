package com.springle.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.springle.security.util.JwtProvider;
import com.springle.user.entity.Role;
import com.springle.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class SecurityTest {

    @Autowired
    private JwtProvider jwtProvider;
    @Value("${jwt.token.secret}") private String secretKey;

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

}
