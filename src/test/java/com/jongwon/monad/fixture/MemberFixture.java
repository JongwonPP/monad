package com.jongwon.monad.fixture;

import com.jongwon.monad.member.domain.Member;

public class MemberFixture {

    public static Member create() {
        return Member.create("test@example.com", "password123", "테스트유저");
    }

    public static Member createWithEmail(String email) {
        return Member.create(email, "password123", "유저_" + email.split("@")[0]);
    }

    public static Member createWithNickname(String nickname) {
        return Member.create("user_" + System.nanoTime() + "@test.com", "password123", nickname);
    }
}
