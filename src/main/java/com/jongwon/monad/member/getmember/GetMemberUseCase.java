package com.jongwon.monad.member.getmember;

import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class GetMemberUseCase {

    private final MemberRepository memberRepository;

    public GetMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public GetMemberResponse execute(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        return new GetMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt()
        );
    }
}
