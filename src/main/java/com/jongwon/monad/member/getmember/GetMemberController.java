package com.jongwon.monad.member.getmember;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetMemberController {

    private final GetMemberUseCase getMemberUseCase;

    public GetMemberController(GetMemberUseCase getMemberUseCase) {
        this.getMemberUseCase = getMemberUseCase;
    }

    @GetMapping("/api/v1/members/{id}")
    public ResponseEntity<GetMemberResponse> getMember(@PathVariable Long id) {
        GetMemberResponse response = getMemberUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
