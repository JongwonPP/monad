package com.jongwon.monad.auth.fake;

import com.jongwon.monad.auth.domain.TokenProvider;

public class FakeTokenProvider implements TokenProvider {

    @Override
    public String generateAccessToken(Long memberId, String email, String nickname) {
        return "access_" + memberId;
    }

    @Override
    public String generateRefreshToken(Long memberId) {
        return "refresh_" + memberId;
    }

    @Override
    public Long getMemberIdFromToken(String token) {
        return Long.valueOf(token.split("_")[1]);
    }

    @Override
    public boolean validateToken(String token) {
        return token != null && token.contains("_");
    }
}
