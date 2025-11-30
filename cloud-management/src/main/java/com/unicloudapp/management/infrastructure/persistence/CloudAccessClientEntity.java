package com.unicloudapp.management.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CloudAccessClientEntity {

    @Id
    private String CloudAccessClientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false)
    private BigDecimal defaultCostLimit;

    @Column(nullable = false)
    private String defaultCleanUpCron;

    @ElementCollection
    @CollectionTable(name = "cloud_resource_types", joinColumns = @JoinColumn(name = "uuid"))
    @Column(name = "cloud_resource_type_uuid", nullable = false)
    private List<String> resourceTypes;
}
