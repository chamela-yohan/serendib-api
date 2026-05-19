package com.serendib.api.service;

import com.serendib.api.config.JwtService;
import com.serendib.api.dto.request.LoginRequest;
import com.serendib.api.dto.request.RegisterRequest;
import com.serendib.api.dto.response.AuthResponse;
import com.serendib.api.dto.response.UserResponse;
import com.serendib.api.entity.User;
import com.serendib.api.exception.BusinessException;
import com.serendib.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // REGISTER
    public AuthResponse register(RegisterRequest request){
        // Check email not taken
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Email already exists" + request.getEmail());
        }

        // Build user - hash the password with BCrypt
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.TRAVELER)
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(savedUser);

        return  AuthResponse.builder().token(token).user(mapToUserResponse(savedUser)).build();
    }

    // LOGIN
    public AuthResponse login(LoginRequest request){
        // Verifies email + password and throws if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here — credentials are valid
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
