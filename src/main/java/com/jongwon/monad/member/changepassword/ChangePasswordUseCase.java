package com.jongwon.monad.member.changepassword;

import com.jongwon.monad.auth.domain.PasswordEncoder;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(Long id, ChangePasswordRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.oldPassword(), member.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다");
        }

        Member.validateRawPassword(request.newPassword());

        member.updatePassword(passwordEncoder.encode(request.newPassword()));
        memberRepository.save(member);
    }
}
