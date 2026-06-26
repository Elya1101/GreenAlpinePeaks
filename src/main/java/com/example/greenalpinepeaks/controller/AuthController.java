package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.AuthRequestDto;
import com.example.greenalpinepeaks.dto.AuthResponseDto;
import com.example.greenalpinepeaks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for login and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
        return authService.login(request);
    }

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody AuthRequestDto request) {
        return authService.register(request);
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public void logout(@RequestHeader("X-User-Id") Long userId) {
    }
}