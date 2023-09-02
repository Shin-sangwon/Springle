package com.springle.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;
import com.springle.member.repository.MemberRepository;
import com.springle.member.service.MemberService;
import com.springle.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AccountServiceTest extends ServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MemberService memberService;

    @DisplayName("회원 회원가입 성공한다")
    @Test
    void userRegistrationSuccess() throws Exception {

        //given
        RegistrationRequest request = new RegistrationRequest("loginId", "loginPassword", "이름", "이메일", Role.GUEST);

        //when
        long id = accountService.join(request);

        //then
        Member member = memberService.findById(id);

        assertThat(member).satisfies(m -> {
            assertThat(m).isNotNull();
            assertThat(m.getLoginId()).isEqualTo("loginId");
        });

    }

}