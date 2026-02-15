package com.jongwon.monad.member.fake;

import com.jongwon.monad.member.domain.Member;
import com.jongwon.monad.member.domain.MemberRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeMemberRepository implements MemberRepository {

    private final Map<Long, Member> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            member.assignId(++sequence);
        }
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return store.values().stream()
                .filter(member -> member.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(member -> member.getEmail().equals(email));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return store.values().stream()
                .anyMatch(member -> member.getNickname().equals(nickname));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
