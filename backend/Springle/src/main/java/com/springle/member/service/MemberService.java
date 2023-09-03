package com.springle.member.service;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.global.exception.ErrorCode;
import com.springle.member.entity.Member;
import com.springle.member.exception.MemberException;
import com.springle.member.repository.MemberQueryRepository;
import com.springle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public long save(RegistrationRequest request) {

        validateDuplicateLoginIdAndEmail(request);
        Member member = request.toEntity(passwordEncoder.encode(request.loginPassword()));

        return memberRepository.save(member)
                               .getId();
    }

    @Transactional(readOnly = true)
    public Member findById(long id) {
        return memberRepository.findById(id)
                               .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                               .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDuplicateLoginIdAndEmail(RegistrationRequest request) {
        checkDuplicateLoginId(request.loginId());
        checkDuplicateEmail(request.email());
    }

    private void checkDuplicateLoginId(String loginId) {
        boolean loginIdExists = memberQueryRepository.existsByLoginId(loginId);

        if (loginIdExists) {
            throw new MemberException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    private void checkDuplicateEmail(String email) {
        boolean emailExists = memberQueryRepository.existsByEmail(email);

        if (emailExists) {
            throw new MemberException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

}
