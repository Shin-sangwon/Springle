package com.springle.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.member.entity.Member;
import com.springle.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class AccountServiceTest extends ServiceTest {

    @Autowired
    private AccountService accountService;

    @Transactional
    @DisplayName("회원 회원가입 성공한다")
    @Test
    void userRegistrationSuccess() throws Exception {

        //given
        RegistrationRequest request = new RegistrationRequest("loginId", "loginPassword", "이름", "이메일");

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