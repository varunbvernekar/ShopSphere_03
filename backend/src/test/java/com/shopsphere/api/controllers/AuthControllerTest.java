package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.responseDTO.UserResponseDTO;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.security.JwtUtils;
import com.shopsphere.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private JwtUtils jwtUtils;

        @Test
        void login_Success() throws Exception {
                User user = User.builder().email("test@test.com").build();
                when(userService.authenticate(anyString(), anyString())).thenReturn(Optional.of(user));
                when(jwtUtils.generateToken(anyString())).thenReturn("mock-token");

                mockMvc.perform(post("/api/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"test@test.com\", \"password\":\"password\"}"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.token").value("mock-token"));
        }
}