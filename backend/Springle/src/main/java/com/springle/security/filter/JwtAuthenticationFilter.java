package com.springle.security.filter;

import com.springle.security.service.UserDetailServiceImpl;
import com.springle.security.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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

        // 유효성 검사
        if (isEmptyOrInvalidAuthorization(authorization)) {
            log.error("authentication is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        if (authorization.startsWith("Bearer ")) {
            // Access Token 처리, 권한 부여하기
            handleAccessToken(token, request, response, filterChain);
        } else if (authorization.startsWith("Refresh ")) {
            // Refresh Token 처리
            handleRefreshToken(token, response);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isEmptyOrInvalidAuthorization(String authorization) {
        return authorization == null || !(authorization.startsWith("Bearer ") || authorization.startsWith("Refresh "));
    }

    private void handleAccessToken(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (jwtProvider.isExpired(token, secretKey)) {
            log.error("access token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        String loginId = jwtProvider.getLoginId(token, secretKey);
        UserDetails user = userDetailService.loadUserByUsername(loginId);

        authorizeUser(loginId, user.getAuthorities(), request);
    }

    private void authorizeUser(String loginId, Collection<? extends GrantedAuthority> authorities, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, null, authorities);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void handleRefreshToken(String token, HttpServletResponse response) {
        try {
            String newAccessToken = jwtProvider.refreshAccessToken(token.substring(8), secretKey);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
        } catch (Exception e) {
            log.error("Failed to refresh access token", e);
        }
    }

}
