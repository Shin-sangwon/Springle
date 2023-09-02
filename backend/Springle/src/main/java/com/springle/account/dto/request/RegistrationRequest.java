package com.springle.account.dto.request;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
    @NotBlank String loginId,
    @NotBlank String loginPassword,
    @NotBlank String name,
    @Email String email
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
