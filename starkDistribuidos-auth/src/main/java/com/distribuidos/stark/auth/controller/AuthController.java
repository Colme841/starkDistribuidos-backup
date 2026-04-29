package com.distribuidos.stark.auth.controller;

import com.distribuidos.stark.auth.model.User;
import com.distribuidos.stark.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new TestResponse("Auth Service is running!", "OK"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("LOGIN REQUEST: username=" + request.username);

        if (request.username == null || request.password == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Username and password required"));
        }

        boolean validAdmin = "admin".equals(request.username) && "admin123".equals(request.password);

        User user = userRepository.findByUsername(request.username).orElse(null);
        boolean validRegisteredUser = user != null && user.password.equals(request.password);

        if (validAdmin || validRegisteredUser) {
            LoginResponse response = new LoginResponse(
                "test_token_" + System.currentTimeMillis(),
                request.username,
                86400000L
            );

            System.out.println("LOGIN SUCCESS for: " + request.username);
            return ResponseEntity.ok(response);
        }

        System.out.println("LOGIN FAILED for: " + request.username);
        return ResponseEntity.status(401).body(new ErrorResponse("Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.username == null || request.email == null || request.password == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Missing fields"));
        }

        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.status(409).body(new ErrorResponse("Username already exists"));
        }

        User user = new User();
        user.username = request.username;
        user.email = request.email;
        user.password = request.password;

        userRepository.save(user);

        return ResponseEntity.ok(new TestResponse("User registered successfully", "OK"));
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        return ResponseEntity.ok(true);
    }

    static class TestResponse {
        public String message;
        public String status;

        public TestResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class RegisterRequest {
        public String username;
        public String email;
        public String password;
    }

    static class LoginResponse {
        public String token;
        public String username;
        public Long expiresIn;

        public LoginResponse(String token, String username, Long expiresIn) {
            this.token = token;
            this.username = username;
            this.expiresIn = expiresIn;
        }
    }

    static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}