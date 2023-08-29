package com.springle.security.configuration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                           .httpBasic(AbstractHttpConfigurer::disable)
                           .cors(Customizer.withDefaults())
                           .headers(headers -> headers.frameOptions(
                               HeadersConfigurer.FrameOptionsConfig::disable))
                           .authorizeHttpRequests(
                               authorize -> authorize.requestMatchers(antMatcher("/api/v1/account/login"))
                                                     .permitAll()
                                                     .anyRequest()
                                                     .authenticated())
                           .sessionManagement(session -> session.sessionCreationPolicy(
                               SessionCreationPolicy.STATELESS))
                           .getOrBuild();

    }
}
