package com.birdbook.controller;

import com.birdbook.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {

        String username = body.getOrDefault("username", "guest");
        String role = body.getOrDefault("role", "BASIC");

        String token = JwtUtil.generateToken(username, role);

        return Map.of("token", token);
    }
}
