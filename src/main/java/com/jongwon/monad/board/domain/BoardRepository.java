package com.jongwon.monad.board.domain;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    Board save(Board board);

    Optional<Board> findById(Long id);

    List<Board> findAll();

    void deleteById(Long id);

    boolean existsByName(String name);
}
