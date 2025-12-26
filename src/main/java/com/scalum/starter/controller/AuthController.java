package com.scalum.starter.controller;

import com.scalum.starter.domain.auth.AuthenticationService;
import com.scalum.starter.dto.ForgotPasswordRequest;
import com.scalum.starter.dto.LoginRequest;
import com.scalum.starter.dto.RefreshTokenRequest;
import com.scalum.starter.dto.ResetPasswordRequest;
import com.scalum.starter.dto.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public AccessTokenResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("refresh-token")
    public AccessTokenResponse refreshToken(
            @RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return authenticationService.refreshToken(refreshTokenRequest.getRefreshToken());
    }

    @PostMapping("forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest.getIdentifier());
        return ResponseEntity.ok().build();
    }

    @PostMapping("reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authenticationService.resetPassword(
                resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        authenticationService.signUp(signUpRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        authenticationService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
}
