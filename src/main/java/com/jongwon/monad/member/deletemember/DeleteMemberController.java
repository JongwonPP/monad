package com.jongwon.monad.member.deletemember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API")
@RestController
public class DeleteMemberController {

    private final DeleteMemberUseCase deleteMemberUseCase;

    public DeleteMemberController(DeleteMemberUseCase deleteMemberUseCase) {
        this.deleteMemberUseCase = deleteMemberUseCase;
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @DeleteMapping("/api/v1/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        deleteMemberUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
