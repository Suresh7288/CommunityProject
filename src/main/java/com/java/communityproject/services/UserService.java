package com.java.communityproject.services;

import com.java.communityproject.config.JwtUtil;
import com.java.communityproject.dtos.LoginRequestDto;
import com.java.communityproject.dtos.LoginResponseDto;
import com.java.communityproject.models.User;
import com.java.communityproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public User registerUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Collections.singleton("MEMBER")); // Default role
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setBio(user.getBio());
        existingUser.setProfilePicture(user.getProfilePicture());
        return userRepository.save(existingUser);
    }
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // Find the user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        return new LoginResponseDto(token);
    }
}
