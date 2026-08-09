package com.kailas.TelemetryHub.security.controller;

import com.kailas.TelemetryHub.entities.RefreshToken;
import com.kailas.TelemetryHub.security.dto.*;
import com.kailas.TelemetryHub.security.jwt.JwtService;
import com.kailas.TelemetryHub.security.service.LoginService;
import com.kailas.TelemetryHub.security.service.RegisterService;
import com.kailas.TelemetryHub.security.service.UserService;
import com.kailas.TelemetryHub.security.user.CustomUserDetails;
import com.kailas.TelemetryHub.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RegisterService registerService;
    private final LoginService loginService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(RegisterService registerService,
                          LoginService loginService,
                          UserService userService,
                          RefreshTokenService refreshTokenService,
                          JwtService jwtService){
        this.registerService = registerService;
        this.loginService = loginService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse token = loginService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(){
        return ResponseEntity.ok(userService.getMe());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> getAccessToken(@RequestBody RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.refreshToken());
        CustomUserDetails user = new CustomUserDetails(refreshToken.getUser());
        String accessToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AccessTokenResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest refreshTokenRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        refreshTokenService.revokeRefreshToken(refreshTokenRequest,customUserDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
