package com.serendib.api.common;

import com.serendib.api.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    // Get the currently logged-in user
    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // The Principal is the User object stored in JwtAuthFilter
        return (User) authentication.getPrincipal();
    }

    // Get just the current user's ID (most common need)
    public java.util.UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }
}