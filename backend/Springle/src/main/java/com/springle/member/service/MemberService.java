package com.springle.member.service;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.member.entity.Member;
import com.springle.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Transactional
    public long save(RegistrationRequest request) {

        Member member = request.toEntity(passwordEncoder.encode(request.loginPassword()), passwordEncoder.encode(request.email()));

        return memberRepository.save(member).getId();
    }

    @Transactional(readOnly = true)
    public Member findById(long id) {
        return memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
