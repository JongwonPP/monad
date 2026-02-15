package com.jongwon.monad.auth.refresh;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshController {

    private final RefreshUseCase refreshUseCase;

    public RefreshController(RefreshUseCase refreshUseCase) {
        this.refreshUseCase = refreshUseCase;
    }

    @PostMapping("/api/v1/auth/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshResponse response = refreshUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
