package com.jongwon.monad.member.signup;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "이메일은 필수입니다")
        String email,
        @NotBlank(message = "비밀번호는 필수입니다")
        String password,
        @NotBlank(message = "닉네임은 필수입니다")
        String nickname
) {}
