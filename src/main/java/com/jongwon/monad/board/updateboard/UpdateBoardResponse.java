package com.jongwon.monad.board.updateboard;

import java.time.LocalDateTime;

public record UpdateBoardResponse(
        Long id,
        String name,
        String description,
        LocalDateTime updatedAt
) {}
