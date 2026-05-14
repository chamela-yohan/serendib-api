package com.serendib.api.service;

import com.serendib.api.entity.User;
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
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(UUID id){
        return userRepository.findById(id);
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
    public User createUser(String name, String email, String password){
        // Check email isn't already taken
        if(emailExists(email))
            throw new RuntimeException("Email already exists: " + email);

        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        // Save
        return userRepository.save(newUser);
    }

    // Delete a user
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
