package com.unicloudapp.management.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.management.domain.CloudResourceAccess;
import com.unicloudapp.management.domain.CloudResourcesAccessStatus;
import com.unicloudapp.management.domain.ExpiresDate;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CloudAccessMapperTest {

    private final CloudAccessMapper mapper = new CloudAccessMapper();

    @Test
    void toDomain_mapsAllFields_fromEntity() {
        // given
        UUID id = UUID.randomUUID();
        String clientId = "client-123";
        String resourceType = "S3";
        BigDecimal costLimit = new BigDecimal("123.45");
        BigDecimal usedLimit = new BigDecimal("67.89");
        LocalDate expiresAt = LocalDate.of(2030, 1, 15);
        String cron = "0 0 12 * * *"; // every day at noon
        CloudResourcesAccessStatus.Status status = CloudResourcesAccessStatus.Status.ACTIVE;

        CloudResourceAccessEntity entity = CloudResourceAccessEntity.builder()
                .cloudResourceAccessId(id)
                .cloudAccessClientId(clientId)
                .resourceType(resourceType)
                .costLimit(costLimit)
                .usedLimit(usedLimit)
                .expiresAt(expiresAt)
                .cronExpression(cron)
                .status(status)
                .build();

        // when
        CloudResourceAccess domain = mapper.toDomain(entity);

        // then
        assertThat(domain.getCloudResourceAccessId().getValue()).isEqualTo(id);
        assertThat(domain.getCloudAccessClientId().getValue()).isEqualTo(clientId);
        assertThat(domain.getCloudResourceType().getName()).isEqualTo(resourceType);
        assertThat(domain.getCostLimit().getCost()).isEqualByComparingTo(costLimit);
        assertThat(domain.getUsedLimit().getValue()).isEqualByComparingTo(usedLimit);
        assertThat(domain.getExpiresAt().getValue()).isEqualTo(expiresAt);
        assertThat(domain.getStatus().getStatus()).isEqualTo(status);
        assertThat(domain.getCronExpression().toString()).isEqualTo(cron);
    }

    @Test
    void toEntity_mapsAllFields_fromDomain_and_roundTripConsistency() {
        // given
        UUID id = UUID.randomUUID();
        String clientId = "client-ABC";
        String resourceType = "EC2";
        BigDecimal costLimit = new BigDecimal("50.00");
        BigDecimal usedLimit = new BigDecimal("10.00");
        LocalDate expiresAt = LocalDate.of(2029, 12, 31);
        String cron = "0 30 8 * * MON-FRI"; // weekdays 08:30
        CloudResourcesAccessStatus.Status status = CloudResourcesAccessStatus.Status.INACTIVE;

        CloudResourceAccess domain = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(id))
                .cloudAccessClientId(CloudAccessClientId.of(clientId))
                .cloudResourceType(CloudResourceType.of(resourceType))
                .costLimit(CostLimit.of(costLimit))
                .usedLimit(UsedLimit.of(usedLimit))
                .expiresAt(ExpiresDate.expirable(expiresAt))
                .status(CloudResourcesAccessStatus.of(status))
                .cronExpression(CronExpression.parse(cron))
                .build();

        // when
        CloudResourceAccessEntity entity = mapper.toEntity(domain);

        // then
        assertThat(entity.getCloudResourceAccessId()).isEqualTo(id);
        assertThat(entity.getCloudAccessClientId()).isEqualTo(clientId);
        assertThat(entity.getResourceType()).isEqualTo(resourceType);
        assertThat(entity.getCostLimit()).isEqualByComparingTo(costLimit);
        assertThat(entity.getUsedLimit()).isEqualByComparingTo(usedLimit);
        assertThat(entity.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(entity.getCronExpression()).isEqualTo(cron);
        assertThat(entity.getStatus()).isEqualTo(status);

        // round-trip: entity -> domain again
        CloudResourceAccess roundTrip = mapper.toDomain(entity);
        assertThat(roundTrip.getCloudResourceAccessId().getValue()).isEqualTo(id);
        assertThat(roundTrip.getCloudAccessClientId().getValue()).isEqualTo(clientId);
        assertThat(roundTrip.getCloudResourceType().getName()).isEqualTo(resourceType);
        assertThat(roundTrip.getCostLimit().getCost()).isEqualByComparingTo(costLimit);
        assertThat(roundTrip.getUsedLimit().getValue()).isEqualByComparingTo(usedLimit);
        assertThat(roundTrip.getExpiresAt().getValue()).isEqualTo(expiresAt);
        assertThat(roundTrip.getStatus().getStatus()).isEqualTo(status);
        assertThat(roundTrip.getCronExpression().toString()).isEqualTo(cron);
    }

    @Test
    void toDomain_throws_when_expiresAt_is_null() {
        // given
        UUID id = UUID.randomUUID();
        String clientId = "client-null-exp";
        String resourceType = "QUEUE";
        BigDecimal costLimit = new BigDecimal("1.00");
        BigDecimal usedLimit = new BigDecimal("0.00");
        String cron = "@daily"; // Spring supports aliases
        CloudResourcesAccessStatus.Status status = CloudResourcesAccessStatus.Status.ACTIVE;

        CloudResourceAccessEntity entity = CloudResourceAccessEntity.builder()
                .cloudResourceAccessId(id)
                .cloudAccessClientId(clientId)
                .resourceType(resourceType)
                .costLimit(costLimit)
                .usedLimit(usedLimit)
                .expiresAt(null)
                .cronExpression(cron)
                .status(status)
                .build();

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mapper.toDomain(entity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expiration date");
    }
}
