package com.jongwon.monad.member.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    void 정상_생성() {
        Member member = Member.create("test@example.com", "password123", "테스트유저");

        assertThat(member.getEmail()).isEqualTo("test@example.com");
        assertThat(member.getPassword()).isEqualTo("password123");
        assertThat(member.getNickname()).isEqualTo("테스트유저");
        assertThat(member.getCreatedAt()).isNotNull();
        assertThat(member.getUpdatedAt()).isNotNull();
    }

    @Test
    void email_null이면_예외() {
        assertThatThrownBy(() -> Member.create(null, "password123", "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email_빈문자열이면_예외() {
        assertThatThrownBy(() -> Member.create("", "password123", "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email_골뱅이_미포함이면_예외() {
        assertThatThrownBy(() -> Member.create("testexample.com", "password123", "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email_100자_초과면_예외() {
        String longEmail = "a".repeat(96) + "@b.co";
        assertThatThrownBy(() -> Member.create(longEmail, "password123", "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void password_null이면_예외() {
        assertThatThrownBy(() -> Member.create("test@example.com", null, "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void password_빈문자열이면_예외() {
        assertThatThrownBy(() -> Member.create("test@example.com", "", "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void password_8자_미만이면_예외() {
        assertThatThrownBy(() -> Member.create("test@example.com", "1234567", "테스트유저"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nickname_null이면_예외() {
        assertThatThrownBy(() -> Member.create("test@example.com", "password123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nickname_빈문자열이면_예외() {
        assertThatThrownBy(() -> Member.create("test@example.com", "password123", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nickname_2자_미만이면_예외() {
        assertThatThrownBy(() -> Member.create("test@example.com", "password123", "a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nickname_20자_초과면_예외() {
        String longNickname = "a".repeat(21);
        assertThatThrownBy(() -> Member.create("test@example.com", "password123", longNickname))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 프로필_수정_성공() {
        Member member = Member.create("test@example.com", "password123", "테스트유저");
        LocalDateTimeSnapshot before = new LocalDateTimeSnapshot(member.getUpdatedAt());

        member.updateProfile("새닉네임");

        assertThat(member.getNickname()).isEqualTo("새닉네임");
        assertThat(member.getUpdatedAt()).isAfterOrEqualTo(before.value());
    }

    @Test
    void 비밀번호_변경_성공() {
        Member member = Member.create("test@example.com", "password123", "테스트유저");
        LocalDateTimeSnapshot before = new LocalDateTimeSnapshot(member.getUpdatedAt());

        member.changePassword("password123", "newpassword123");

        assertThat(member.getPassword()).isEqualTo("newpassword123");
        assertThat(member.getUpdatedAt()).isAfterOrEqualTo(before.value());
    }

    @Test
    void 비밀번호_변경_시_기존_비밀번호_불일치면_예외() {
        Member member = Member.create("test@example.com", "password123", "테스트유저");

        assertThatThrownBy(() -> member.changePassword("wrongpassword", "newpassword123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 비밀번호가 일치하지 않습니다");
    }

    private record LocalDateTimeSnapshot(java.time.LocalDateTime value) {}
}
