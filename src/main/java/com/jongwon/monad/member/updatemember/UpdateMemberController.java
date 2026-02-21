package com.jongwon.monad.member.updatemember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API")
@RestController
public class UpdateMemberController {

    private final UpdateMemberUseCase updateMemberUseCase;

    public UpdateMemberController(UpdateMemberUseCase updateMemberUseCase) {
        this.updateMemberUseCase = updateMemberUseCase;
    }

    @Operation(summary = "프로필 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "본인만 수정 가능")
    })
    @PutMapping("/api/v1/members/{id}")
    public ResponseEntity<UpdateMemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request) {
        UpdateMemberResponse response = updateMemberUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}
