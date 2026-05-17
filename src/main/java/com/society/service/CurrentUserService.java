package com.society.service;

import com.society.entity.User;
import com.society.repository.UserRepository;
import com.society.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        try {
            String token = authHeader.substring(7);
            if (!jwtUtils.validateToken(token)) {
                return Optional.empty();
            }
            String email = jwtUtils.extractUsername(token);
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isSocietyScopedAdmin(User user) {
        return user != null
                && user.getRole() == User.Role.SOCIETY_ADMIN
                && user.getSocietyId() != null;
    }
}
