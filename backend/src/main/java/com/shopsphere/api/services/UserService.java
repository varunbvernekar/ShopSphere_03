package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.RegisterRequestDTO;
import com.shopsphere.api.dto.requestDTO.UserUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.UserResponseDTO;
import com.shopsphere.api.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    UserResponseDTO registerUser(RegisterRequestDTO request);

    Optional<User> authenticate(String email, String password);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updateRequest);
}
