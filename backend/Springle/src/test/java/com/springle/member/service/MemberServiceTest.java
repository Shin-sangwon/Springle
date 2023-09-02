package com.springle.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.springle.member.entity.Member;
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

}