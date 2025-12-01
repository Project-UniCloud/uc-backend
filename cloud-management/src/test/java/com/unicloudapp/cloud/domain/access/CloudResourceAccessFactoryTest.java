package com.unicloudapp.cloud.domain.access;

import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.cloud.domain.vo.ExpiresDate;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.scheduling.support.CronExpression;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CloudResourceAccessFactoryTest {

    private final CloudResourceAccessFactory factory = new CloudResourceAccessFactory();

    @Test
    @DisplayName("create returns entity with provided fields and defaulted values")
    void create_happyPath_setsFieldsAndDefaults() {
        CloudResourceAccessId id = CloudResourceAccessId.of(UUID.randomUUID());
        CloudConnectorId connectorId = CloudConnectorId.of("conn-1");
        CloudResourceType type = CloudResourceType.of("S3");
        CostLimit cost = CostLimit.of(new BigDecimal("123.45"));
        CronExpression cron = CronExpression.parse("0 */15 * * * *");
        ExpiresDate expires = ExpiresDate.of(java.time.LocalDate.now().plusDays(7));

        CloudResourceAccess access = factory.create(id, connectorId, type, cost, cron, expires);

        // Provided fields
        assertEquals(id, access.getCloudResourceAccessId());
        assertEquals(connectorId, access.getCloudConnectorId());
        assertEquals(type, access.getCloudResourceType());
        assertEquals(cost, access.getCostLimit());
        assertEquals(cron, access.getCronExpression());
        assertEquals(expires, access.getExpiresAt());

        // Defaults from factory
        assertEquals(UsedLimit.empty().getValue(), access.getUsedLimit().getValue());
        assertEquals(CloudResourcesAccessStatus.Status.INACTIVE, access.getStatus().getStatus());
        assertEquals(50, access.getNotificationLevel1().level());
        assertEquals(80, access.getNotificationLevel2().level());
        assertEquals(95, access.getNotificationLevel3().level());
    }

    @Test
    @DisplayName("create allows null cron and expires (kept null)")
    void create_allowsNullCronAndExpires() {
        CloudResourceAccessId id = CloudResourceAccessId.of(UUID.randomUUID());
        CloudConnectorId connectorId = CloudConnectorId.of("conn-2");
        CloudResourceType type = CloudResourceType.of("EC2");
        CostLimit cost = CostLimit.of(BigDecimal.ZERO);

        CloudResourceAccess access = factory.create(id, connectorId, type, cost, null, null);

        assertNull(access.getCronExpression());
        assertNull(access.getExpiresAt());
        assertEquals(connectorId, access.getCloudConnectorId());
        assertEquals(type, access.getCloudResourceType());
        assertEquals(cost, access.getCostLimit());
    }

    @ParameterizedTest
    @MethodSource("invalidRequiredArgs")
    @DisplayName("create throws IllegalArgumentException when required args are null")
    void create_throwsOnNullRequired(CloudResourceAccessId id,
                                     CloudConnectorId connectorId,
                                     CloudResourceType type,
                                     CostLimit cost) {
        assertThrows(IllegalArgumentException.class, () ->
                factory.create(id, connectorId, type, cost, null, null)
        );
    }

    private static Stream<Arguments> invalidRequiredArgs() {
        return Stream.of(
                Arguments.of(null, CloudConnectorId.of("x"), CloudResourceType.of("S3"), CostLimit.of(BigDecimal.ONE)),
                Arguments.of(CloudResourceAccessId.of(UUID.randomUUID()), null, CloudResourceType.of("S3"), CostLimit.of(BigDecimal.ONE)),
                Arguments.of(CloudResourceAccessId.of(UUID.randomUUID()), CloudConnectorId.of("x"), null, CostLimit.of(BigDecimal.ONE)),
                Arguments.of(CloudResourceAccessId.of(UUID.randomUUID()), CloudConnectorId.of("x"), CloudResourceType.of("S3"), null)
        );
    }
}
