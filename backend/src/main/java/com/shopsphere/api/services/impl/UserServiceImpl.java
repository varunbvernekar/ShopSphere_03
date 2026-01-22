package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.UserUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.UserResponseDTO;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.repositories.UserRepository;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponseDTO registerUser(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Registration failed: Email {} already taken", user.getEmail());
            throw new RuntimeException("Email already taken");
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        log.debug("Authenticating user: {}", email);
        Optional<User> user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
        if (user.isPresent()) {
            log.info("Authentication successful for email: {}", email);
        } else {
            log.warn("Authentication failed for email: {}", email);
        }
        return user;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        log.debug("Fetching user profile for ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });

        checkAccess(user);

        return UserResponseDTO.fromEntity(user);
    }

    private void checkAccess(User targetUser) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        // If system call or anonymous (shouldn't happen due to security filter),
        // careful.
        if (auth == null || !auth.isAuthenticated())
            return;

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String currentUserEmail = auth.getName();
            if (!currentUserEmail.equals(targetUser.getEmail())) {
                log.warn("Access Denied: User {} tried to access data of user {}", currentUserEmail,
                        targetUser.getEmail());
                throw new RuntimeException("Access Denied: You do not have permission to view/edit this profile.");
            }
        }
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updateRequest) {
        log.info("Updating user profile for ID: {}", id);
        User existing = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found for update with ID: {}", id);
                    return new RuntimeException("User not found");
                });

        checkAccess(existing);

        if (updateRequest != null) {
            if (updateRequest.getName() != null)
                existing.setName(updateRequest.getName());
            if (updateRequest.getPhoneNumber() != null)
                existing.setPhoneNumber(updateRequest.getPhoneNumber());
            if (updateRequest.getAddress() != null)
                existing.setAddress(updateRequest.getAddress());
            if (updateRequest.getGender() != null)
                existing.setGender(updateRequest.getGender());
            if (updateRequest.getDateOfBirth() != null)
                existing.setDateOfBirth(updateRequest.getDateOfBirth());
        }

        User savedUser = userRepository.save(existing);
        log.info("User profile updated successfully for ID: {}", id);
        return UserResponseDTO.fromEntity(savedUser);
    }
}
