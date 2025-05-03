package com.apica.user_service.config;

import com.apica.user_service.entity.Roles;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.utils.CustomIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    RolesRepository roleRepo;
    UserRepository userRepo;
    PasswordEncoder passwordEncoder;

    public DataInitializer(RolesRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1) ensure ROLE_ADMIN exists
        Optional<Roles> adminRole = roleRepo.findByName("ROLE_ADMIN")
                .or(() -> {
                    Roles role = new Roles();
                    role.setName("ROLE_ADMIN");
                    return Optional.of(roleRepo.save(role));
                });

        // 2) ensure “admin” user exists
        userRepo.findByUsername("admin").or(() -> {
            Users u = new Users();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("password"));
            u.setFullName("Administrator");
            u.setRoles(Set.of(adminRole.get()));
            return Optional.of(userRepo.save(u));
        });
    }
}
