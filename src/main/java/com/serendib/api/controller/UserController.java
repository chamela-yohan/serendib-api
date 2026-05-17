package com.serendib.api.controller;

import com.serendib.api.common.ApiResponse;
import com.serendib.api.dto.request.CreateUserRequest;
import com.serendib.api.dto.response.UserResponse;
import com.serendib.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/v1/users
    // Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(){
       List<UserResponse> users =  userService.getAllUsers();
       return ResponseEntity.ok(
               ApiResponse.success("Successfully retrieved users", users)
       );
    }

    // GET /api/v1/users/{id}
    // Get single user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id){
       UserResponse user =  userService.getUserById(id);
       return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    // POST /api/v1/users
    // Create new user
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid CreateUserRequest request){
      UserResponse user =  userService.createUser(request);
      // 201 Created
      return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created successfully", user));
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully")
        );
    }

}
