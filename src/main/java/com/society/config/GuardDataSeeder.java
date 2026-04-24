package com.society.config;

import com.society.entity.User;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GuardDataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if guards already exist
        List<User> existingGuards = userRepository.findByRole(User.Role.GUARD);
        
        if (existingGuards.isEmpty()) {
            System.out.println("Seeding dummy guards...");
            seedGuards();
        } else {
            System.out.println("Guards already exist. Skipping seeding.");
        }
    }

    private void seedGuards() {
        List<User> guards = new ArrayList<>();

        // Guard 1
        User guard1 = new User();
        guard1.setName("Rajesh Kumar");
        guard1.setEmail("rajesh.guard@society.com");
        guard1.setPassword(passwordEncoder.encode("123456"));
        guard1.setRole(User.Role.GUARD);
        guard1.setStatus(User.UserStatus.ACTIVE);
        guard1.setPhoneNumber("9876543210");
        guard1.setFlatNumber("Gate 1");
        guards.add(guard1);

        // Guard 2
        User guard2 = new User();
        guard2.setName("Suresh Singh");
        guard2.setEmail("suresh.guard@society.com");
        guard2.setPassword(passwordEncoder.encode("123456"));
        guard2.setRole(User.Role.GUARD);
        guard2.setStatus(User.UserStatus.ACTIVE);
        guard2.setPhoneNumber("9876543211");
        guard2.setFlatNumber("Gate 2");
        guards.add(guard2);

        // Guard 3
        User guard3 = new User();
        guard3.setName("Amit Patel");
        guard3.setEmail("amit.guard@society.com");
        guard3.setPassword(passwordEncoder.encode("123456"));
        guard3.setRole(User.Role.GUARD);
        guard3.setStatus(User.UserStatus.ACTIVE);
        guard3.setPhoneNumber("9876543212");
        guard3.setFlatNumber("Main Gate");
        guards.add(guard3);

        // Guard 4
        User guard4 = new User();
        guard4.setName("Vikram Sharma");
        guard4.setEmail("vikram.guard@society.com");
        guard4.setPassword(passwordEncoder.encode("123456"));
        guard4.setRole(User.Role.GUARD);
        guard4.setStatus(User.UserStatus.ACTIVE);
        guard4.setPhoneNumber("9876543213");
        guard4.setFlatNumber("Parking Gate");
        guards.add(guard4);

        userRepository.saveAll(guards);
        System.out.println("Successfully seeded 4 dummy guards!");
    }
}
