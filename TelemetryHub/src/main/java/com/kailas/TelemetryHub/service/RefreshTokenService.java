package com.kailas.TelemetryHub.service;

import com.kailas.TelemetryHub.entities.RefreshToken;
import com.kailas.TelemetryHub.exception.InvalidRefreshTokenException;
import com.kailas.TelemetryHub.repository.RefreshTokenRepository;
import com.kailas.TelemetryHub.repository.UserRepository;
import com.kailas.TelemetryHub.security.dto.RefreshTokenRequest;
import com.kailas.TelemetryHub.security.user.CustomUserDetails;
import com.kailas.TelemetryHub.security.user.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,UserRepository userRepository){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;

    }

    public RefreshToken createRefreshToken(User user){
        RefreshToken rfToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiry_date(Instant.now().plusSeconds(60*60*24*7))
                .user(user).build();
        refreshTokenRepository.save(rfToken);
        return rfToken;
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new InvalidRefreshTokenException());

        if(refreshToken.getExpiry_date().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException();
        }

        return refreshToken;

    }

    public void revokeRefreshToken(RefreshTokenRequest request,CustomUserDetails userDetails){
        RefreshToken rfToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(()->new InvalidRefreshTokenException());

        User user = userDetails.getUser();

        if(!(rfToken.getUser().getId() == user.getId())){
            throw new InvalidRefreshTokenException();
        }
        refreshTokenRepository.delete(rfToken);
    }


}
