package com.jongwon.monad.member.signup;

import com.jongwon.monad.auth.domain.PasswordEncoder;
import com.jongwon.monad.global.exception.DuplicateException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class SignUpUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpUseCase(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SignUpResponse execute(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateException("이미 사용 중인 이메일입니다");
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new DuplicateException("이미 사용 중인 닉네임입니다");
        }

        Member.validateRawPassword(request.password());

        Member member = Member.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()
        );
        Member saved = memberRepository.save(member);

        return new SignUpResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getNickname(),
                saved.getCreatedAt()
        );
    }
}
