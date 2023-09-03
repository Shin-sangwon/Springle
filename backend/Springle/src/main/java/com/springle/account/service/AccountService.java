package com.springle.account.service;

import com.springle.account.dto.request.LoginRequest;
import com.springle.account.dto.request.RegistrationRequest;
import com.springle.account.dto.response.TokenResponse;
import com.springle.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final MemberService memberService;

    public long join(RegistrationRequest request) {

        return memberService.save(request);
    }

    public TokenResponse login(LoginRequest loginRequest) {

        return new TokenResponse("access", "refresh");
    }
}
