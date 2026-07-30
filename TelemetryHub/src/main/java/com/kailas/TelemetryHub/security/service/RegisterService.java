package com.kailas.TelemetryHub.security.service;

import com.kailas.TelemetryHub.repository.UserRepository;
import com.kailas.TelemetryHub.security.dto.RegisterRequest;
import com.kailas.TelemetryHub.security.user.Role;
import com.kailas.TelemetryHub.security.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest){
        if(userRepository.existsByUsername(registerRequest.username())){
            throw new RuntimeException("Username already exists");

        }
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole(Role.VIEWER);
        user.setEnabled(true);
        userRepository.save(user);


    }
}
