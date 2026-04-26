package com.samir.backend.controller;

import com.samir.backend.dto.AuthResponse;
import com.samir.backend.dto.LoginRequest;
import com.samir.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Important pour ton futur frontend (React/Angular)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public AuthResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. On vérifie si l'utilisateur existe et si le mot de passe est bon
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. On enregistre cette authentification dans le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. On génère le fameux Token JWT
        String jwt = jwtUtils.generateToken(authentication.getName());

        // 4. On renvoie le Token au client (PowerShell, Postman, ou Frontend)
        return new AuthResponse(jwt);
    }
}