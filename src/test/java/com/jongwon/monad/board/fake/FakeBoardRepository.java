package com.jongwon.monad.board.fake;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;

import java.util.*;

public class FakeBoardRepository implements BoardRepository {

    private final Map<Long, Board> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Board save(Board board) {
        if (board.getId() == null) {
            board.assignId(++sequence);
        }
        store.put(board.getId(), board);
        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Board> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsByName(String name) {
        return store.values().stream()
                .anyMatch(board -> board.getName().equals(name));
    }
}
