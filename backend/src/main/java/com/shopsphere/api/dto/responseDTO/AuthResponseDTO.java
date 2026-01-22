package com.shopsphere.api.dto.responseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private UserResponseDTO user;
}
