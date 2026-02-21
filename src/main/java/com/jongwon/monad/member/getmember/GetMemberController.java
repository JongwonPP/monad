package com.jongwon.monad.member.getmember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API")
@RestController
public class GetMemberController {

    private final GetMemberUseCase getMemberUseCase;

    public GetMemberController(GetMemberUseCase getMemberUseCase) {
        this.getMemberUseCase = getMemberUseCase;
    }

    @Operation(summary = "프로필 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @GetMapping("/api/v1/members/{id}")
    public ResponseEntity<GetMemberResponse> getMember(@PathVariable Long id) {
        GetMemberResponse response = getMemberUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
