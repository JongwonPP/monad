package com.jongwon.monad.auth.login;

import com.jongwon.monad.auth.domain.PasswordEncoder;
import com.jongwon.monad.auth.domain.TokenProvider;
import com.jongwon.monad.auth.fake.FakePasswordEncoder;
import com.jongwon.monad.auth.fake.FakeTokenProvider;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.infra.FakeMemberRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginUseCaseTest {

    private LoginUseCase useCase;
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        passwordEncoder = new FakePasswordEncoder();
        tokenProvider = new FakeTokenProvider();
        useCase = new LoginUseCase(memberRepository, passwordEncoder, tokenProvider);
    }

    @Test
    void 로그인_성공() {
        Member member = Member.create("test@example.com", passwordEncoder.encode("password123"), "테스트유저");
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("test@example.com", "password123");

        LoginResponse response = useCase.execute(request);

        assertThat(response.accessToken()).isEqualTo("access_" + member.getId());
        assertThat(response.refreshToken()).isEqualTo("refresh_" + member.getId());
    }

    @Test
    void 존재하지_않는_이메일이면_예외() {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("존재하지 않는 이메일입니다");
    }

    @Test
    void 비밀번호_불일치면_예외() {
        Member member = Member.create("test@example.com", passwordEncoder.encode("password123"), "테스트유저");
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }
}
