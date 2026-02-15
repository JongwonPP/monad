package com.jongwon.monad.member.updatemember;

import jakarta.validation.constraints.NotBlank;

public record UpdateMemberRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        String nickname
) {}
