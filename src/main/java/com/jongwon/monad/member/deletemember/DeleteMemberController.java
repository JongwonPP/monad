package com.jongwon.monad.member.deletemember;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteMemberController {

    private final DeleteMemberUseCase deleteMemberUseCase;

    public DeleteMemberController(DeleteMemberUseCase deleteMemberUseCase) {
        this.deleteMemberUseCase = deleteMemberUseCase;
    }

    @DeleteMapping("/api/v1/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        deleteMemberUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
