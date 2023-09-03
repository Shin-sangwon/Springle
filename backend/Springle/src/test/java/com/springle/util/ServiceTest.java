package com.springle.util;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.member.repository.MemberRepository;
import com.springle.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class ServiceTest {

    @Autowired
    protected MemberService memberService;
    @Autowired
    protected MemberRepository memberRepository;

    protected long createMember() {

        RegistrationRequest request = new RegistrationRequest("testId", "testPassword", "testName", "testEmail@email.com");

        return memberService.save(request);
    }

}
