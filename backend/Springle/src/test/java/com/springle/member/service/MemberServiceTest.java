package com.springle.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.springle.member.entity.Member;
import com.springle.member.exception.MemberException;
import com.springle.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class MemberServiceTest extends ServiceTest {

    @Transactional
    @DisplayName("회원 조회된다")
    @Test
    void canFindExistingMember() throws Exception {

        long id = createMember();

        Member member = memberService.findById(id);

        assertThat(member).isNotNull();
    }

    @DisplayName("존재하지 않는 회원 MemberException 던진다")
    @Test
    void findNotExistsMemberThrowsMemberException() throws Exception{

        assertThrows(MemberException.class, () -> memberService.findById(77777L));
    }

}