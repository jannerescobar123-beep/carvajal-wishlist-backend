package com.carvajal.wishlist.config;

import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin_janner").ifPresentOrElse(
                user -> {
                    if (user.getRole() != Role.ADMIN) {
                        user.setRole(Role.ADMIN);
                        userRepository.save(user);
                        log.info("Usuario 'admin_janner' promovido exitosamente a rol ADMIN");
                    }
                },
                () -> {
                    User admin = new User(
                        "admin_janner",
                        "admin@carvajal.com",
                        passwordEncoder.encode("superpassword123"),
                        Role.ADMIN
                    );
                    userRepository.save(admin);
                    log.info("Usuario inicial 'admin_janner' creado con rol ADMIN");
                }
            );
        };
    }
}
