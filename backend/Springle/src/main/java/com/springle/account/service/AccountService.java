package com.springle.account.service;

import com.springle.account.dto.request.RegistrationRequest;
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
}
