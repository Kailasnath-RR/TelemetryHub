package com.kailas.TelemetryHub.security.service;

import com.kailas.TelemetryHub.security.dto.UserResponse;
import com.kailas.TelemetryHub.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserResponse getMe(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user =(CustomUserDetails)auth.getPrincipal();
        return new UserResponse(user.getUsername(),user.getRole(), user.isEnabled());
    }

}
