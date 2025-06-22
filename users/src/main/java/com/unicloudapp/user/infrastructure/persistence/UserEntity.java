package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.common.domain.user.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@Getter
@Setter(AccessLevel.PROTECTED)
class UserEntity {

    @Id
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole.Type role;
}
