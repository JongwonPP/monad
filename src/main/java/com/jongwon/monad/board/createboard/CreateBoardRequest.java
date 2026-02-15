package com.jongwon.monad.board.createboard;

import jakarta.validation.constraints.NotBlank;

public record CreateBoardRequest(
        @NotBlank(message = "게시판 이름은 필수입니다")
        String name,
        String description
) {}
