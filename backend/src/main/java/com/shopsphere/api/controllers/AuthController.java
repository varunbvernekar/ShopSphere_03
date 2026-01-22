package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.LoginRequestDTO;
import com.shopsphere.api.dto.requestDTO.RegisterRequestDTO;
import com.shopsphere.api.dto.responseDTO.AuthResponseDTO;
import com.shopsphere.api.dto.responseDTO.UserResponseDTO;
import com.shopsphere.api.entity.User;
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
    private final com.shopsphere.api.security.JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        log.info("Received registration request for email: {}", request.getEmail());
        User userEntity = mapToEntity(request);
        UserResponseDTO createdUser = userService.registerUser(userEntity);

        String token = jwtUtils.generateToken(createdUser.getEmail());
        log.info("User registered successfully: {}", createdUser.getEmail());
        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(token)
                .user(createdUser)
                .build());
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

    private User mapToEntity(RegisterRequestDTO req) {
        return User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .role(com.shopsphere.api.enums.UserRole.CUSTOMER)
                .gender(req.getGender())
                .dateOfBirth(req.getDateOfBirth())
                .build();
    }
}
