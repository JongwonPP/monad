package com.jongwon.monad.global.security;

public record AuthenticationPrincipal(Long memberId, String email, String nickname) {
}
