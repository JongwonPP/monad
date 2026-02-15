package com.jongwon.monad.member.signup;

import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignUpUseCaseTest {

    private SignUpUseCase useCase;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        useCase = new SignUpUseCase(memberRepository);
    }

    @Test
    void 회원가입_성공() {
        SignUpRequest request = new SignUpRequest("test@example.com", "password123", "테스트유저");

        SignUpResponse response = useCase.execute(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.nickname()).isEqualTo("테스트유저");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 이메일_중복_시_예외() {
        memberRepository.save(MemberFixture.createWithEmail("test@example.com"));

        SignUpRequest request = new SignUpRequest("test@example.com", "password123", "다른유저");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다");
    }

    @Test
    void 닉네임_중복_시_예외() {
        memberRepository.save(MemberFixture.createWithNickname("테스트유저"));

        SignUpRequest request = new SignUpRequest("new@example.com", "password123", "테스트유저");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인 닉네임입니다");
    }
}
