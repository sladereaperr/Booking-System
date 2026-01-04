package com.booking.backend.controller;

import com.booking.backend.model.User;
import com.booking.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserRepository userRepository;

    // Get My Profile
    @GetMapping("/me")
    public User getMyProfile(@AuthenticationPrincipal String email) {
        // The "email" string comes directly from the JWT token via JwtFilter
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Update My Profile (Name, Phone)
    @PutMapping("/me")
    public User updateProfile(@AuthenticationPrincipal String email, @RequestBody User updatedData) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (updatedData.getName() != null) user.setName(updatedData.getName());
        if (updatedData.getPhone() != null) user.setPhone(updatedData.getPhone());
        
        return userRepository.save(user);
    }
}