package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.LoginRequestDTO;
import com.shopsphere.api.dto.requestDTO.RegisterRequestDTO;
import com.shopsphere.api.dto.responseDTO.AuthResponseDTO;
import com.shopsphere.api.dto.responseDTO.UserResponseDTO;

import com.shopsphere.api.security.JwtUtils;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        log.info("Received registration request for email: {}", request.getEmail());
        UserResponseDTO createdUser = userService.registerUser(request);

        log.info("User registered successfully: {}", createdUser.getEmail());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        log.info("Received login request for email: {}", request.getEmail());
        return userService.authenticate(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getEmail());
                    log.info("User logged in successfully: {}", user.getEmail());
                    return ResponseEntity.ok(AuthResponseDTO.builder()
                            .token(token)
                            .user(UserResponseDTO.fromEntity(user))
                            .build());
                })
                .orElseGet(() -> {
                    log.warn("Login failed for email: {}", request.getEmail());
                    return ResponseEntity.status(401).build();
                });
    }

}
