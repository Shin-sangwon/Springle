package com.springle.member.repository;

import static com.springle.member.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean existsByLoginId(String loginId) {
        return queryFactory.from(member)
                           .where(member.loginId.eq(loginId))
                           .select(member.id)
                           .fetchFirst() != null;
    }

    public boolean existsByEmail(String email) {
        return queryFactory.from(member)
                           .where(member.email.eq(email))
                           .select(member.id)
                           .fetchFirst() != null;
    }

}
