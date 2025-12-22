package com.unicloudapp.cloud.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cloud_connectors")
class CloudConnectorEntity {

    @Id
    private String id;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cloud_connectors_resource_types", joinColumns = @JoinColumn(name = "cloud_connector_id"))
    @Column(name = "resource_type", nullable = false)
    private List<String> resourceTypes;
}
