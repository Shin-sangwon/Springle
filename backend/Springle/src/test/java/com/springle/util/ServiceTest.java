package com.springle.util;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;
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

        Member member = Member.builder()
                              .loginId("testId")
                              .email("testEmail")
                              .loginPassword("testPassword")
                              .role(Role.USER)
                              .name("testName")
                              .build();

        return memberRepository.save(member).getId();
    }

}
