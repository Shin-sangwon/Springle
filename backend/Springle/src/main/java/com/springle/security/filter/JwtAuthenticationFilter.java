package com.springle.security.filter;

import com.springle.security.service.UserDetailServiceImpl;
import com.springle.security.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailServiceImpl userDetailService;
    private final JwtProvider jwtProvider;
    @Value("${jwt.token.secret}") private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("authorization : '{}'", authorization);

        if (authorization == null || !(authorization.startsWith("Bearer ") || authorization.startsWith("Refresh "))) {
            log.error("authentication is null");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7); // "Bearer " 또는 "Refresh " 이후의 토큰 부분만 추출
        if (authorization.startsWith("Bearer ")) {
            // Access Token 처리
            if (jwtProvider.isExpired(token, secretKey)) {
                log.error("access token is expired");
                filterChain.doFilter(request, response);
                return;
            }

            String loginId = jwtProvider.getLoginId(token, secretKey);
            UserDetails user = userDetailService.loadUserByUsername(loginId);

            // 권한 부여하기
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginId, null, user.getAuthorities());

            // Detail
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext()
                                 .setAuthentication(authenticationToken);


//        } else if (authorization.startsWith("Refresh ")) {
//            // Refresh Token 처리
//            String refreshToken = authorization.substring(8); // "Refresh " prefix 제거
//            try {
//                String newAccessToken = jwtProvider.refreshAccessToken(refreshToken, secretKey);
//                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
//            } catch (Exception e) {
//                // Refresh Token 검증 실패 또는 새로운 Access Token 발급 실패
//                log.error("Failed to refresh access token", e);
//            }

//        }

        filterChain.doFilter(request, response);
    }

}
}
