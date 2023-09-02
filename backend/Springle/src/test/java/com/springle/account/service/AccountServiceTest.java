package com.springle.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.member.entity.Member;
import com.springle.member.exception.MemberException;
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
        RegistrationRequest request = new RegistrationRequest("loginId", "loginPassword", "이름", "email@email.com");

        //when
        long id = accountService.join(request);

        //then
        Member member = memberService.findById(id);

        assertThat(member).satisfies(m -> {
            assertThat(m).isNotNull();
            assertThat(m.getLoginId()).isEqualTo("loginId");
        });

    }

    @DisplayName("회원 정보 암호화 된다")
    @Test
    void memberInfoIsEncrypted() throws Exception {

        //given
        RegistrationRequest request = new RegistrationRequest("loginId", "loginPassword", "이름", "email@email.com");

        //when
        long id = accountService.join(request);

        //then
        Member member = memberService.findById(id);

        assertThat(member).satisfies(m -> {
            assertThat(m.getLoginPassword()).isNotEqualTo("loginPassword");
            assertThat(m.getEmail()).isEqualTo("email@email.com");
        });
    }

    @Transactional
    @DisplayName("중복된 아이디 회원가입 실패한다")
    @Test
    void duplicatedInformationFailedJoin() throws Exception {
        //given
        RegistrationRequest request = new RegistrationRequest("loginId", "loginPassword", "이름", "email@email.com");

        //when
        long id = accountService.join(request);

        assertThrows(MemberException.class, () -> accountService.join(request));
    }

}