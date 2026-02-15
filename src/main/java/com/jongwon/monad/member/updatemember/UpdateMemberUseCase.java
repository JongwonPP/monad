package com.jongwon.monad.member.updatemember;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberUseCase {

    private final MemberRepository memberRepository;

    public UpdateMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public UpdateMemberResponse execute(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        member.updateProfile(request.nickname());
        Member saved = memberRepository.save(member);

        return new UpdateMemberResponse(
                saved.getId(),
                saved.getNickname(),
                saved.getUpdatedAt()
        );
    }
}
