package com.unicloudapp.users.infrastructure.persistent;

import com.unicloudapp.users.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
class UserEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
