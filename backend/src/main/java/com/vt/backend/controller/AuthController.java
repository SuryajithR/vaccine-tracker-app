package com.vt.backend.controller;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.vt.backend.auth.AuthService;
import com.vt.backend.dto.*;
import com.vt.backend.entity.User;
import com.vt.backend.repo.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository userRepo;
  private final AuthService authService;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(UserRepository userRepo, AuthService authService) {
    this.userRepo = userRepo;
    this.authService = authService;
  }

  @PostMapping("/register")
  public String register(@RequestBody RegisterRequest req) {
    if (req.email == null || req.password == null || req.name == null) {
      throw new RuntimeException("Missing fields");
    }

    Optional<User> exists = userRepo.findByEmail(req.email);
    if (exists.isPresent()) throw new RuntimeException("Email already registered");

    User u = new User();
    u.setName(req.name.trim());
    u.setEmail(req.email.trim().toLowerCase());
    u.setPasswordHash(encoder.encode(req.password));

    userRepo.save(u);
    return "OK";
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest req) {
    User u = userRepo.findByEmail(req.email.trim().toLowerCase())
        .orElseThrow(() -> new RuntimeException("Invalid credentials"));

    if (!encoder.matches(req.password, u.getPasswordHash())) {
      throw new RuntimeException("Invalid credentials");
    }

    String token = authService.createSession(u.getId());
    return new AuthResponse(token, u.getId(), u.getName());
  }
}
