package com.society.config;

import com.society.entity.User;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if Super Admin already exists
        if (userRepository.findByEmail("patelhimanshu5636@gmail.com").isPresent()) {
            System.out.println("Super Admin already exists. Skipping seeding.");
            return;
        }

        // Create Super Admin
        User superAdmin = new User();
        superAdmin.setName("Super Admin");
        superAdmin.setEmail("patelhimanshu5636@gmail.com");
        superAdmin.setPassword(passwordEncoder.encode("patel@09"));
        superAdmin.setRole(User.Role.SUPER_ADMIN);
        superAdmin.setStatus(User.UserStatus.ACTIVE);

        userRepository.save(superAdmin);
        System.out.println("Super Admin created successfully with email: patelhimanshu5636@gmail.com");
    }
}
