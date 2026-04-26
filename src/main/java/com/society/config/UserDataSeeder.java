package com.society.config;

import com.society.entity.User;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        User existingAdmin = userRepository.findByEmail("admin@nivas.com").orElse(null);
        
        if (existingAdmin == null) {
            System.out.println("Seeding admin user...");
            seedAdminUser();
        } else {
            System.out.println("Admin user already exists. Skipping seeding.");
        }
    }

    private void seedAdminUser() {
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@nivas.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setStatus(User.UserStatus.ACTIVE);
        admin.setPhoneNumber("9999999999");
        admin.setFlatNumber("Admin Office");
        
        userRepository.save(admin);
        System.out.println("Successfully seeded admin user!");
    }
}
