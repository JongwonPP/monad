package com.jongwon.monad.auth.login;

import com.jongwon.monad.auth.domain.PasswordEncoder;
import com.jongwon.monad.auth.domain.TokenProvider;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public LoginUseCase(MemberRepository memberRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse execute(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("존재하지 않는 이메일입니다"));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }

        String accessToken = tokenProvider.generateAccessToken(member.getId(), member.getEmail(), member.getNickname());
        String refreshToken = tokenProvider.generateRefreshToken(member.getId());

        return new LoginResponse(accessToken, refreshToken);
    }
}
