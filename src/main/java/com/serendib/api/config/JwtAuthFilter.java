package com.serendib.api.config;

import com.serendib.api.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)  // filterChain = next()
            throws ServletException, IOException {

        // 1- Read Authorization header
        // Like: req.headers.authorization in Express
        final String authHeader = request.getHeader("Authorization");

        // 2- if no token -> skip (let SecurityConfig decide if route is public)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // next()
            return;
        }

        //3- Extract token (remove "Bearer " prefix)
        final String jwt = authHeader.substring(7);

        try {
            // 4- Extract email from token
            final String email = jwtService.extractUsername(jwt);

            // 5- If email found and not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6- Load user from DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 7- Validation token
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 8- Tell "this user is authenticated"
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }

            }

        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            // Don't throw - let the request continue unauthenticated
        }

        filterChain.doFilter(request, response);
    }
}
