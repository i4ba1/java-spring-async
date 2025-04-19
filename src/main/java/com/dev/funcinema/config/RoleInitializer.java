package com.dev.funcinema.config;


import com.dev.funcinema.model.Role;
import com.dev.funcinema.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            // Initialize roles if they don't exist
            if (roleRepository.count() == 0) {
                log.info("Initializing roles...");

                roleRepository.save(Role.builder().name(Role.ERole.ROLE_USER).build());
                roleRepository.save(Role.builder().name(Role.ERole.ROLE_MODERATOR).build());
                roleRepository.save(Role.builder().name(Role.ERole.ROLE_ADMIN).build());

                log.info("Roles initialized successfully");
            }
        };
    }
}
