package com.jongwon.monad.auth.login;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
