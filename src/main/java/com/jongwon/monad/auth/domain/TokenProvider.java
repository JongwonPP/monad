package com.jongwon.monad.auth.domain;

public interface TokenProvider {

    String generateAccessToken(Long memberId, String email, String nickname);

    String generateRefreshToken(Long memberId);

    Long getMemberIdFromToken(String token);

    boolean validateToken(String token);
}
