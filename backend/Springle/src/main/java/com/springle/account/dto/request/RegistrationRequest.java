package com.springle.account.dto.request;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
    @NotBlank String loginId,
    @NotBlank String loginPassword,
    @NotBlank String name,
    @Email @NotBlank String email
) {

    public Member toEntity(String encodePassword) {
        return Member.builder()
                     .loginId(this.loginId)
                     .loginPassword(encodePassword)
                     .name(this.name)
                     .email(this.email)
                     .role(Role.USER)
                     .build();
    }

}
