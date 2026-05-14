package com.serendib.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "users")


@Data               // generates: getters, setters, toString, equals, hashCode
@Builder            // generates: User.builder().name("x").email("y").build()
@NoArgsConstructor  // generates: new User()  (empty constructor)
@AllArgsConstructor // generates: new User(id, name, email, ...)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // @PrePersist: runs automatically BEFORE INSERT
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // @PreUpdate: runs automatically BEFORE UPDATE
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}