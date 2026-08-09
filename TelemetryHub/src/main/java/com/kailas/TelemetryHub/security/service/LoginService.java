package com.kailas.TelemetryHub.security.service;

import com.kailas.TelemetryHub.entities.RefreshToken;
import com.kailas.TelemetryHub.security.dto.LoginRequest;
import com.kailas.TelemetryHub.security.dto.LoginResponse;
import com.kailas.TelemetryHub.security.jwt.JwtService;
import com.kailas.TelemetryHub.security.user.CustomUserDetails;
import com.kailas.TelemetryHub.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginService(AuthenticationManager authenticationManager,JwtService jwtService,RefreshTokenService refreshTokenService){
        this.authenticationManager = authenticationManager;
        this.jwtService=jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest request){
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(request.username(),request.password()));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());


        return new LoginResponse(jwtService.generateToken(userDetails),
                refreshToken.getToken());


    }
}
