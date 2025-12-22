package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.common.vo.user.UserRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_uuid"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRole.Type> roles;
}
