package com.jongwon.monad.auth.refresh;

import com.jongwon.monad.auth.domain.TokenProvider;
import com.jongwon.monad.auth.fake.FakeTokenProvider;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshUseCaseTest {

    private RefreshUseCase useCase;
    private MemberRepository memberRepository;
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        tokenProvider = new FakeTokenProvider();
        useCase = new RefreshUseCase(tokenProvider, memberRepository);
    }

    @Test
    void 토큰_갱신_성공() {
        Member member = Member.create("test@example.com", "encoded_password123", "테스트유저");
        memberRepository.save(member);

        String refreshToken = tokenProvider.generateRefreshToken(member.getId());
        RefreshRequest request = new RefreshRequest(refreshToken);

        RefreshResponse response = useCase.execute(request);

        assertThat(response.accessToken()).isEqualTo("access_" + member.getId());
    }

    @Test
    void 유효하지_않은_토큰이면_예외() {
        RefreshRequest request = new RefreshRequest("invalidtoken");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 토큰입니다");
    }
}
