package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.RegisterRequestDTO;
import com.shopsphere.api.dto.requestDTO.UserUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.UserResponseDTO;
import com.shopsphere.api.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    UserResponseDTO registerUser(User user); // Controller converts Request -> Entity for now, or change?
    // Let's keep consistency with earlier step where controller mapped.
    // Actually, `registerUser` in Controller currently maps DTO->Entity.
    // Let's stick to that for now to minimize churn, OR use RegisterRequestDTO here?
    // Interface in step 123 was 'UserDTO registerUser(User user)'.

    Optional<User> authenticate(String email, String password);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updateRequest);
}
