package com.hugo.mabibli.controller;

import com.hugo.mabibli.dto.UserResponse;
import com.hugo.mabibli.entity.User;
import com.hugo.mabibli.security.UserPrincipal;
import com.hugo.mabibli.service.AccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hugo.mabibli.dto.DeleteAccountRequest;
import com.hugo.mabibli.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/me")
public class MeController {
    private final AccountService accountService;

    public MeController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public UserResponse me(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User user = principal.getUser();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DeleteAccountRequest request
    ) {
        accountService.deleteAccount(
                principal.getUser(),
                request
        );

        return ResponseEntity.noContent().build();
    }
}