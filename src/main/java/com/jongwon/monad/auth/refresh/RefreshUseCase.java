package com.jongwon.monad.auth.refresh;

import com.jongwon.monad.auth.domain.TokenProvider;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class RefreshUseCase {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public RefreshUseCase(TokenProvider tokenProvider, MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    public RefreshResponse execute(RefreshRequest request) {
        if (!tokenProvider.validateToken(request.refreshToken())) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다");
        }

        Long memberId = tokenProvider.getMemberIdFromToken(request.refreshToken());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        String accessToken = tokenProvider.generateAccessToken(member.getId(), member.getEmail(), member.getNickname());

        return new RefreshResponse(accessToken);
    }
}
