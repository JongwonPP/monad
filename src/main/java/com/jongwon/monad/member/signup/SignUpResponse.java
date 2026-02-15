package com.jongwon.monad.member.signup;

import java.time.LocalDateTime;

public record SignUpResponse(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt
) {}
