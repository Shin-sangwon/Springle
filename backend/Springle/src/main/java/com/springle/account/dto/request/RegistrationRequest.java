package com.springle.account.dto.request;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;

public record RegistrationRequest(
    String loginId,
    String loginPassword,
    String name,
    String email
) {

    public Member toEntity() {
        return Member.builder()
                     .loginId(this.loginId)
                     .loginPassword(this.loginPassword)
                     .name(this.name)
                     .email(this.email)
                     .role(Role.USER)
                     .build();
    }

}
