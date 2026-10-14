package com.example.demo.dto;

import com.example.demo.model.User;

public record AuthResponse(Long id, String name, String email, String role) {

    public static AuthResponse from(User user, String role) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), role);
    }
}
