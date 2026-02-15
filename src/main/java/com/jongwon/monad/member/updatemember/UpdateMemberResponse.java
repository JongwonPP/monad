package com.jongwon.monad.member.updatemember;

import java.time.LocalDateTime;

public record UpdateMemberResponse(
        Long id,
        String nickname,
        LocalDateTime updatedAt
) {}
