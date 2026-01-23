package com.birdbook.controller;

import com.birdbook.models.Role;
import com.birdbook.models.User;
import com.birdbook.security.JwtUtil;
import com.birdbook.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse response) {

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username and password are required"));
        }

        User user = userService.getByUsername(username)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(
            user.getId().toHexString(),
            user.getUsername(),
            user.getRole().name()
        );

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(864000);
        cookie.setDomain("localhost");
        response.addCookie(cookie);
        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "role", user.getRole().name(),
                        "username", user.getUsername()
                )
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");

        if (username == null || password == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username and password are required"));
        }

        if (userService.getByUsername(username).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "username already exists"));
        }

        User savedUser = userService.registerUser(username, password);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                    "role", savedUser.getRole().name(),
                    "username", savedUser.getUsername()
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;

        if(cookies != null){
            for(Cookie cookie : cookies) {
                if("jwt".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null || token.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","No JWT token found"));
        }

        try{
            String userId = jwtUtil.extractUserId(token);
            User user = userService.getUserById(new ObjectId(userId));
            if(user == null){
                return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "User not found"));
            }
            return ResponseEntity.ok(
                Map.of(
                    "id", user.getId().toHexString(),
                    "username", user.getUsername(),
                    "role", user.getRole().name()
                )
            );
        } catch (Exception e) {
            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
        }
    }
}
