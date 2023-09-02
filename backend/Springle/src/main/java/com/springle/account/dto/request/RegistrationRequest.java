package com.springle.account.dto.request;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;

public record RegistrationRequest(
    String loginId,
    String loginPassword,
    String name,
    String email
) {

    public Member toEntity(String encodePassword, String encodeEmail) {
        return Member.builder()
                     .loginId(this.loginId)
                     .loginPassword(encodePassword)
                     .name(this.name)
                     .email(encodeEmail)
                     .role(Role.USER)
                     .build();
    }

}
