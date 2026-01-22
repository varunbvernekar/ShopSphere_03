package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.UserUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.UserResponseDTO;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        log.debug("Fetching user details for ID: {}", id);
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            log.error("User not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
            @RequestBody UserUpdateRequestDTO userUpdateRequest) {
        log.info("Updating user profile for ID: {}", id);
        try {
            return ResponseEntity.ok(userService.updateUser(id, userUpdateRequest));
        } catch (RuntimeException e) {
            log.error("Failed to update user ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
