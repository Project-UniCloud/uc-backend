package com.unicloudapp.cloud.infrastructure.persistence;

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
    private String CloudVendorConnectorId;

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
    @CollectionTable(
            name = "cloud_access_client_resource_types",
            joinColumns = @JoinColumn(name = "client_id")
    )
    @Column(name = "resource_type", nullable = false)
    private List<String> resourceTypes;
}
