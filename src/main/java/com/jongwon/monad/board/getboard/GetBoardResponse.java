package com.jongwon.monad.board.getboard;

import java.time.LocalDateTime;

public record GetBoardResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {}
