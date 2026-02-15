package com.jongwon.monad.member.getmember;

import java.time.LocalDateTime;

public record GetMemberResponse(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt
) {}
