package com.shopsphere.api.controllers;

import com.shopsphere.api.entity.User;
import com.shopsphere.api.services.CartService;
import com.shopsphere.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CartService cartService;
    @MockBean private UserService userService;

    @Test
    @WithMockUser(username = "test@user.com")
    void getCart_Success() throws Exception {
        User user = User.builder().id(1L).email("test@user.com").build();
        when(userService.findByEmail("test@user.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk());
    }
}