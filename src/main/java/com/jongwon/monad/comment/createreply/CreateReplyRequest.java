package com.jongwon.monad.comment.createreply;

import jakarta.validation.constraints.NotBlank;

public record CreateReplyRequest(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {}
