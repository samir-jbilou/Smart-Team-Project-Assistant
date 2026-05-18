// AuthController.java
package com.samir.backend.controller;

import com.samir.backend.dto.AuthResponse;
import com.samir.backend.dto.LoginRequest;
import com.samir.backend.dto.RegisterRequest;
import com.samir.backend.entity.User;
import com.samir.backend.entity.enums.UserRole;
import com.samir.backend.repository.UserRepository;
import com.samir.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication.getName());
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return new AuthResponse(jwt, role);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if ("CHEF_DE_PROJET".equals(registerRequest.getRole())) {
            user.setRole(UserRole.CHEF_DE_PROJET);
        } else {
            user.setRole(UserRole.MEMBRE);
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}