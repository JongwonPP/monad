package com.jongwon.monad.member.changepassword;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "기존 비밀번호는 필수입니다")
        String oldPassword,
        @NotBlank(message = "새 비밀번호는 필수입니다")
        String newPassword
) {}
