package com.jongwon.monad.member.updatemember;

import com.jongwon.monad.fixture.MemberFixture;
import com.jongwon.monad.global.exception.EntityNotFoundException;
import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;
import com.jongwon.monad.member.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateMemberUseCaseTest {

    private UpdateMemberUseCase useCase;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        useCase = new UpdateMemberUseCase(memberRepository);
    }

    @Test
    void 수정_성공() {
        Member saved = memberRepository.save(MemberFixture.create());

        UpdateMemberRequest request = new UpdateMemberRequest("새닉네임");

        UpdateMemberResponse response = useCase.execute(saved.getId(), request);

        assertThat(response.id()).isEqualTo(saved.getId());
        assertThat(response.nickname()).isEqualTo("새닉네임");
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_회원_수정시_예외() {
        UpdateMemberRequest request = new UpdateMemberRequest("새닉네임");

        assertThatThrownBy(() -> useCase.execute(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }
}
