package com.serendib.api.service;

import com.serendib.api.dto.CreateUserRequest;
import com.serendib.api.dto.UserResponse;
import com.serendib.api.entity.User;
import com.serendib.api.exception.BusinessException;
import com.serendib.api.exception.ResourceNotFoundException;
import com.serendib.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    // 'final' + @RequiredArgsConstructor: Spring automatically injects User Repository here. No 'new UserRepository()' needed(DEPENDENCY INJECTION)
    private final UserRepository userRepository;

    // Get all users
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // Get user by ID
    public UserResponse getUserById(UUID id){
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(()->
                    new ResourceNotFoundException("User", id)
                );
    }

    // Get user by Email
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    // Check if email exists
    public boolean emailExists(String email){
        return userRepository.existsByEmail(email);
    }

    // Create new user
    public UserResponse createUser(CreateUserRequest request){
        // Check email isn't already taken
        if(emailExists(request.getEmail()))
            throw new BusinessException(
                    "Email already exists: " + request.getEmail()
            );

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        // Save
        User savedUser = userRepository.save(newUser);

        return mapToResponse(savedUser);
    }

    // Delete a user
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }


    // Private mapper
    // Converts Entity -> Response DTO
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
