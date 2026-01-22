package com.shopsphere.api.config;

import com.shopsphere.api.enums.UserRole;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> adminOptional = userRepository.findByEmail("admin@shopsphere.com");
        if (adminOptional.isEmpty()) {
            User admin = User.builder()
                    .name("Main Admin")
                    .email("admin@shopsphere.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .phoneNumber("0000000000")
                    .build();
            userRepository.save(admin);
            System.out.println("Default Admin account created: admin@shopsphere.com / admin123");
        } else {
            User admin = adminOptional.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            System.out.println("Admin password updated to encoded 'admin123'");
        }
    }
}
