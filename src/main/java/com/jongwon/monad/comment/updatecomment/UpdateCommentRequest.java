package com.jongwon.monad.comment.updatecomment;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {}
