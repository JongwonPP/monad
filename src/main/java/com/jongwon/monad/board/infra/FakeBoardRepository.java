package com.jongwon.monad.board.infra;

import com.jongwon.monad.board.domain.Board;
import com.jongwon.monad.board.domain.BoardRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("local")
public class FakeBoardRepository implements BoardRepository {

    private final Map<Long, Board> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public Board save(Board board) {
        if (board.getId() == null) {
            board.assignId(sequence.incrementAndGet());
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
