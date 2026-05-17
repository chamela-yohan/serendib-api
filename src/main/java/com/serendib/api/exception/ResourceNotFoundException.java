package com.serendib.api.exception;

// Thrown when something doesn't exist in DB
// Like: user not found, trip not found etc.
// 404 Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Convenience constructor
    // Usage: new ResourceNotFoundException("User" , id)
    // Produce: "User not found with id"
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }

}
