package com.jongwon.monad.member.signup;

import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class SignUpUseCase {

    private final MemberRepository memberRepository;

    public SignUpUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public SignUpResponse execute(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }

        Member member = Member.create(request.email(), request.password(), request.nickname());
        Member saved = memberRepository.save(member);

        return new SignUpResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNickname(),
                saved.getCreatedAt()
        );
    }
}
