package com.society.service;

import com.society.dto.AuthResponse;
import com.society.dto.LoginRequest;
import com.society.dto.RegisterRequest;
import com.society.entity.User;
import com.society.repository.UserRepository;
import com.society.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not active. Please contact admin.");
        }

        String jwt = jwtUtils.generateToken(userDetails);

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getFlatNumber(),
                user.getStatus().name()
        );
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check if flat number already exists (only for residents)
        if (registerRequest.getFlatNumber() != null && !registerRequest.getFlatNumber().isEmpty()) {
            if (userRepository.existsByFlatNumber(registerRequest.getFlatNumber())) {
                throw new RuntimeException("Flat already registered.");
            }
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.Role.valueOf(registerRequest.getRole()));
        user.setFlatNumber(registerRequest.getFlatNumber());
        user.setPhoneNumber(registerRequest.getPhoneNumber());

        if (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.GUARD || user.getRole() == User.Role.SUPER_ADMIN) {
            user.setStatus(User.UserStatus.ACTIVE);
        } else {
            user.setStatus(User.UserStatus.PENDING);
        }

        User savedUser = userRepository.save(user);

        String jwt = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole().name(),
                savedUser.getFlatNumber(),
                savedUser.getStatus().name()
        );
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
