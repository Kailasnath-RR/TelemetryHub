package com.kailas.TelemetryHub.security.controller;

import com.kailas.TelemetryHub.security.dto.RegisterRequest;
import com.kailas.TelemetryHub.security.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegisterController {
    private final RegisterService registerService;
    public RegisterController(RegisterService registerService){
        this.registerService = registerService;

    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
