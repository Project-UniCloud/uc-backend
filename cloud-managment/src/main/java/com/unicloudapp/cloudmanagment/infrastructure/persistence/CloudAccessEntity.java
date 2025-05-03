package com.unicloudapp.cloudmanagment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CloudAccessEntity {

    @Id
    private UUID cloudAccessId;

    @Column(nullable = false)
    private String cloudAccessClientId;

    @Column(nullable = false)
    private String externalUserId;

    @Column(nullable = false)
    private UUID userId;
}
