package com.distribuidos.stark.auth.config;

import com.distribuidos.stark.auth.model.User;
import com.distribuidos.stark.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            // Si no existen usuarios, crear uno por defecto
            if (userRepository.findByUsername("testuser").isEmpty()) {
                User testUser = new User();
                testUser.username = "testuser";
                testUser.email = "test@starkindustries.com";
                testUser.password = "test123";
                userRepository.save(testUser);
                System.out.println("✅ Usuario de prueba creado: testuser / test123");
            }

            // Usuario admin
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.username = "admin";
                adminUser.email = "admin@starkindustries.com";
                adminUser.password = "admin123";
                userRepository.save(adminUser);
                System.out.println("✅ Usuario admin creado: admin / admin123");
            }
        };
    }
}
