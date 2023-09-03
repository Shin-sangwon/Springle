package com.springle.account.service;

import com.springle.account.dto.request.LoginRequest;
import com.springle.account.dto.request.RegistrationRequest;
import com.springle.account.dto.response.TokenResponse;
import com.springle.global.exception.ErrorCode;
import com.springle.member.entity.Member;
import com.springle.member.exception.MemberException;
import com.springle.member.service.MemberService;
import com.springle.security.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public long join(RegistrationRequest request) {

        return memberService.save(request);
    }

    public TokenResponse login(LoginRequest loginRequest) {

        Member member = memberService.findByLoginId(loginRequest.loginId());
        validatePasswordCheck(member.getLoginPassword(), loginRequest.loginPassword());

        String accessToken = jwtProvider.createToken(member.getId(), member.getLoginId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getLoginId());

        return new TokenResponse(accessToken, refreshToken);
    }

    private void validatePasswordCheck(String memberPassword, String requestPassword) {
        if(!passwordEncoder.matches(requestPassword, memberPassword)) {
            throw new MemberException(ErrorCode.INVALIDATED_PASSWORD);
        }
    }
}
