package com.jongwon.monad.member.updatemember;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateMemberController {

    private final UpdateMemberUseCase updateMemberUseCase;

    public UpdateMemberController(UpdateMemberUseCase updateMemberUseCase) {
        this.updateMemberUseCase = updateMemberUseCase;
    }

    @PutMapping("/api/v1/members/{id}")
    public ResponseEntity<UpdateMemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request) {
        UpdateMemberResponse response = updateMemberUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}
