package com.jongwon.monad.member.getmember;

import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetMemberUseCaseTest {

    private GetMemberUseCase useCase;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        useCase = new GetMemberUseCase(memberRepository);
    }

    @Test
    void 조회_성공() {
        Member saved = memberRepository.save(MemberFixture.create());

        GetMemberResponse response = useCase.execute(saved.getId());

        assertThat(response.id()).isEqualTo(saved.getId());
        assertThat(response.email()).isEqualTo(saved.getEmail());
        assertThat(response.nickname()).isEqualTo(saved.getNickname());
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_회원_조회시_예외() {
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }
}
