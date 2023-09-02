package com.springle.member.service;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.member.entity.Member;
import com.springle.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public long save(RegistrationRequest request) {
        return memberRepository.save(request.toEntity()).getId();
    }

    public Member findById(long id) {
        return memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
