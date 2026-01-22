package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.entity.Address;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Address address;
    private String gender;
    private LocalDate dateOfBirth;
}
