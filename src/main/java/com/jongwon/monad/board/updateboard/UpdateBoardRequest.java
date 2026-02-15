package com.jongwon.monad.board.updateboard;

import jakarta.validation.constraints.NotBlank;

public record UpdateBoardRequest(
        @NotBlank(message = "게시판 이름은 필수입니다")
        String name,
        String description
) {}
