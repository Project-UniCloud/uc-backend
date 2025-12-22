package com.unicloudapp.cloud.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

class CloudResourceAccessClientMapperTest {

    private final CloudResourceAccessClientMapper mapper = new CloudResourceAccessClientMapper() {};

    @Test
    void toEntity_mapsAllFields_fromDomain() {
        // given
        String id = "connector-1";
        String name = "AWS S3";
        String host = "s3.amazonaws.com";
        Integer port = 443;
        BigDecimal costLimit = new BigDecimal("100.00");
        String cron = "0 0 0 * * *";
        List<CloudResourceType> resourceTypes = List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2"));

        CloudConnector domain = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of(id))
                .name(name)
                .host(host)
                .port(port)
                .defaultCostLimit(CostLimit.of(costLimit))
                .cronExpression(CronExpression.parse(cron))
                .resourceTypes(resourceTypes)
                .build();

        // when
        CloudConnectorEntity entity = mapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getHost()).isEqualTo(host);
        assertThat(entity.getPort()).isEqualTo(port);
        assertThat(entity.getDefaultCostLimit()).isEqualByComparingTo(costLimit);
        assertThat(entity.getDefaultCleanUpCron()).isEqualTo(cron);
        assertThat(entity.getResourceTypes()).containsExactly("S3", "EC2");
    }

    @Test
    void toDomain_mapsAllFields_fromEntity() {
        // given
        String id = "connector-2";
        String name = "Azure Blob";
        String host = "blob.core.windows.net";
        Integer port = 80;
        BigDecimal costLimit = new BigDecimal("50.50");
        String cron = "0 0 12 * * *";
        List<String> resourceTypes = List.of("BLOB", "VM");

        CloudConnectorEntity entity = CloudConnectorEntity.builder()
                .id(id)
                .name(name)
                .host(host)
                .port(port)
                .defaultCostLimit(costLimit)
                .defaultCleanUpCron(cron)
                .resourceTypes(resourceTypes)
                .build();

        // when
        CloudConnector domain = mapper.toDomain(entity);

        // then
        assertThat(domain.getCloudConnectorId().id()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo(name);
        assertThat(domain.getHost()).isEqualTo(host);
        assertThat(domain.getPort()).isEqualTo(port);
        assertThat(domain.getDefaultCostLimit().getCost()).isEqualByComparingTo(costLimit);
        assertThat(domain.getCronExpression().toString()).isEqualTo(cron);
        assertThat(domain.getResourceTypes().stream()
                        .map(CloudResourceType::getName)
                        .collect(Collectors.toList()))
                .containsExactly("BLOB", "VM");
    }

    @Test
    void toEntity_returnsNull_whenInputIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toDomain_returnsNull_whenInputIsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_handlesNullFields() {
        // given
        CloudConnector domain = CloudConnector.builder().build();

        // when
        CloudConnectorEntity entity = mapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isNull();
        assertThat(entity.getHost()).isNull();
        assertThat(entity.getPort()).isNull();
        assertThat(entity.getDefaultCostLimit()).isNull();
        assertThat(entity.getDefaultCleanUpCron()).isNull();
        assertThat(entity.getResourceTypes()).isNull();
    }

    @Test
    void toDomain_handlesNullFields() {
        // given
        CloudConnectorEntity entity = CloudConnectorEntity.builder().build();

        // when
        CloudConnector domain = mapper.toDomain(entity);

        // then
        assertThat(domain.getCloudConnectorId()).isNull();
        assertThat(domain.getName()).isNull();
        assertThat(domain.getHost()).isNull();
        assertThat(domain.getPort()).isNull();
        assertThat(domain.getDefaultCostLimit()).isNull();
        assertThat(domain.getCronExpression()).isNull();
        assertThat(domain.getResourceTypes()).isNull();
    }
}
