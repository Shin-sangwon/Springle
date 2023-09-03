package com.springle.account.controller;

import com.springle.account.dto.request.LoginRequest;
import com.springle.account.dto.request.RegistrationRequest;
import com.springle.account.dto.response.TokenResponse;
import com.springle.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(RegistrationRequest request) {
        log.debug("AccountController - join");
        accountService.join(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest) {
        log.debug("AccountController - login");
        TokenResponse tokenResponse = accountService.login(loginRequest);

        return ResponseEntity.ok().body(tokenResponse);
    }
    //TODO : Login - token, Update, findMember, tokenRefresh, logout, verifyEmail
}
