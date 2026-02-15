package com.jongwon.monad.member.changepassword;

import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangePasswordUseCaseTest {

    private ChangePasswordUseCase useCase;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        useCase = new ChangePasswordUseCase(memberRepository);
    }

    @Test
    void 비밀번호_변경_성공() {
        Member saved = memberRepository.save(MemberFixture.create());

        ChangePasswordRequest request = new ChangePasswordRequest("password123", "newPassword456");

        useCase.execute(saved.getId(), request);

        Member updated = memberRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPassword()).isEqualTo("newPassword456");
    }

    @Test
    void 기존_비밀번호_불일치_시_예외() {
        Member saved = memberRepository.save(MemberFixture.create());

        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword456");

        assertThatThrownBy(() -> useCase.execute(saved.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존 비밀번호가 일치하지 않습니다");
    }

    @Test
    void 존재하지_않는_회원_비밀번호_변경시_예외() {
        ChangePasswordRequest request = new ChangePasswordRequest("password123", "newPassword456");

        assertThatThrownBy(() -> useCase.execute(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }
}
