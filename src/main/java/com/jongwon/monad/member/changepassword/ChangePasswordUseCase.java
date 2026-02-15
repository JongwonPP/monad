package com.jongwon.monad.member.changepassword;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordUseCase {

    private final MemberRepository memberRepository;

    public ChangePasswordUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void execute(Long id, ChangePasswordRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        member.changePassword(request.oldPassword(), request.newPassword());
        memberRepository.save(member);
    }
}
