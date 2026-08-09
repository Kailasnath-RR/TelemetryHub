package com.kailas.TelemetryHub.security.dto;

import com.kailas.TelemetryHub.security.user.Role;

public record UserResponse(
        String username,
        Role role,
        boolean enabled
) {
}
