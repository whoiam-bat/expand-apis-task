package com.example.task.service;

import com.example.task.model.User;
import com.example.task.model.dto.AuthRequest;
import com.example.task.model.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authManager;


    public AuthResponse register(AuthRequest authRequest) {
        User user = User.builder()
                .username(authRequest.getUsername())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .build();

        userService.add(user);

        String token = jwtService.generateToken(user);

        return constructResponse(token);
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        User user = userService.findByUsername(authRequest.getUsername());
        String token = jwtService.generateToken(user);

        return constructResponse(token);
    }

    private AuthResponse constructResponse(String token) {
        return AuthResponse.builder().token(token).build();
    }

}
