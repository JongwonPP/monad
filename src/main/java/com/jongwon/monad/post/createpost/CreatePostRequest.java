package com.jongwon.monad.post.createpost;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest(
        @NotBlank(message = "게시글 제목은 필수입니다")
        String title,
        @NotBlank(message = "게시글 본문은 필수입니다")
        String content
) {}
