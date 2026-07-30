package com.kailas.TelemetryHub.security.service;

import com.kailas.TelemetryHub.repository.UserRepository;
import com.kailas.TelemetryHub.security.user.CustomUserDetails;
import com.kailas.TelemetryHub.security.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->
                        new UsernameNotFoundException("Username not found: "+ username));

        return new CustomUserDetails(user);
    }
}
