package com.springle.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.springle.account.dto.request.LoginRequest;
import com.springle.account.dto.request.RegistrationRequest;
import com.springle.account.dto.response.TokenResponse;
import com.springle.account.exception.AccountException;
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

    @Transactional
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

        assertThrows(AccountException.class, () -> accountService.join(request));
    }

    @Transactional
    @DisplayName("회원 로그인 성공한다")
    @Test
    void successMemberLogin() throws Exception {

        //given
        long id = createMember();
        LoginRequest loginRequest = new LoginRequest("testId", "testPassword");
        //when
        TokenResponse tokenResponse = accountService.login(loginRequest);
        System.out.println(tokenResponse.toString());
        //then
        assertThat(tokenResponse).satisfies(token -> {
            assertThat(token.accessToken()).isNotNull();
            assertThat(token.refreshToken()).isNotNull();
        });

    }

    @Transactional
    @DisplayName("없는 아이디로 로그인시 MemberException 반환한다")
    @Test
    void throwAccountExceptionWhenLoginWithNonExistentId() throws Exception {

        //given
        LoginRequest loginRequest = new LoginRequest("nonExists", "1234");

        //when
        //then, findByLoginId로 entity를 찾지 못하는 것은 일반적으로도 사용되므로 MemberException을 던지는데, 로그인 용 메소드를 만들어서 AccountException을 던지게 해야 하나?
        assertThrows(MemberException.class, () -> accountService.login(loginRequest));
    }

    @Transactional
    @DisplayName("잘못된 비밀번호 로그인 시 AccountException 반환한다")
    @Test
    void throwAccountExceptionWhenLoginWithWrongPassword() throws Exception {

        //given
        createMember();

        //when
        LoginRequest loginRequest = new LoginRequest("testId", "wrongPassword");

        //then
        assertThrows(AccountException.class, () -> accountService.login(loginRequest));
    }

}