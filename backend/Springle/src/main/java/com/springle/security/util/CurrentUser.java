package com.springle.security.util;

import com.springle.member.entity.Member;
import com.springle.member.entity.Role;

public record CurrentUser(
    long id,
    String loginId,
    Role role
) {
    public CurrentUser(Member member) {
        this (
            member.getId(),
            member.getLoginId(),
            member.getRole()
        );
    }
}
