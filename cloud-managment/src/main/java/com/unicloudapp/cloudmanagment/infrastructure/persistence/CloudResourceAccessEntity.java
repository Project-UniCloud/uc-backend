package com.unicloudapp.cloudmanagment.infrastructure.persistence;

import com.unicloudapp.cloudmanagment.domain.CloudResourcesAccessStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CloudResourceAccessEntity {

    @Id
    private UUID cloudResourceAccessId;

    @Column(nullable = false)
    private String cloudAccessClientId;

    @Column(nullable = false)
    private String resourceType;

    @Column(nullable = false)
    private BigDecimal costLimit;

    @Column(nullable = false)
    private BigDecimal usedLimit;

    @Column
    private LocalDate expiresAt;

    @Column(nullable = false)
    private String cronExpression;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CloudResourcesAccessStatus.Status status;
}
