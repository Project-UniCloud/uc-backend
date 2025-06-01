package com.unicloudapp.cloudmanagment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
}
