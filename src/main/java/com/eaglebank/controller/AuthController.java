
package com.eaglebank.controller;

import com.eaglebank.dto.*;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {

        var user = repo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid");
        }

        return new AuthResponse(jwt.generateToken(user.getUsername()));
    }
}
