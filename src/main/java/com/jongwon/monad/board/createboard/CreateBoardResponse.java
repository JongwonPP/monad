package com.jongwon.monad.board.createboard;

import java.time.LocalDateTime;

public record CreateBoardResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {}
