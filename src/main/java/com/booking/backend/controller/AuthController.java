package com.booking.backend.controller;

import com.booking.backend.dto.LoginRequest;
import com.booking.backend.dto.RefreshTokenRequest;
import com.booking.backend.dto.RegisterRequest;
import com.booking.backend.model.User;
import com.booking.backend.repository.UserRepository;
import com.booking.backend.util.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.booking.backend.model.Role;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String accessToken = jwtUtil.generateToken(request.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());
            return ResponseEntity.ok(java.util.Map.of(
                "accessToken", accessToken, 
                "refreshToken", refreshToken
            ));
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        if (jwtUtil.validateToken(request.getRefreshToken())) {
            String email = jwtUtil.extractEmail(request.getRefreshToken());
            String newAccessToken = jwtUtil.generateToken(email);
            return ResponseEntity.ok(java.util.Map.of("accessToken", newAccessToken));
        }
        throw new RuntimeException("Invalid refresh token");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}