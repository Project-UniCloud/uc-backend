package com.unicloudapp.management.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.management.domain.CloudResourceAccess;
import com.unicloudapp.management.domain.CloudResourcesAccessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlCloudResourceAccessRepositoryAdapterTest {

    @Mock
    private CloudAccessMapper mapper;

    @Mock
    private CloudAccessJpaRepository repository;

    @InjectMocks
    private SqlCloudResourceAccessRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<Iterable<UUID>> uuidIterableCaptor;

    private UUID id1;
    private UUID id2;
    private CloudResourceAccess domain1;
    private CloudResourceAccess domain2;
    private CloudResourceAccessEntity entity1;
    private CloudResourceAccessEntity entity2;

    @BeforeEach
    void init() {
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();

        entity1 = entity(id1, "client-A", "S3", new BigDecimal("10"), new BigDecimal("3"), LocalDate.of(2030, 1, 1), "@daily", CloudResourcesAccessStatus.Status.ACTIVE);
        entity2 = entity(id2, "client-B", "EC2", new BigDecimal("20"), new BigDecimal("5"), null, "0 0 * * * *", CloudResourcesAccessStatus.Status.INACTIVE);

        domain1 = domain(id1, "client-A", "S3", new BigDecimal("10"), new BigDecimal("3"), LocalDate.of(2030, 1, 1), "@daily", CloudResourcesAccessStatus.Status.ACTIVE);
        domain2 = domain(id2, "client-B", "EC2", new BigDecimal("20"), new BigDecimal("5"), null, "0 0 * * * *", CloudResourcesAccessStatus.Status.INACTIVE);
    }

    @Test
    void save_delegates_to_mapper_and_repository() {
        // given
        when(mapper.toEntity(domain1)).thenReturn(entity1);

        // when
        adapter.save(domain1);

        // then
        verify(mapper).toEntity(domain1);
        verify(repository).save(entity1);
    }

    @Test
    void getCloudResourceAccesses_translatesIds_and_mapsToSet() {
        // given
        Set<CloudResourceAccessId> ids = Set.of(CloudResourceAccessId.of(id1), CloudResourceAccessId.of(id2));
        when(repository.findAllById(any(Iterable.class))).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(domain1);
        when(mapper.toDomain(entity2)).thenReturn(domain2);

        // when
        Set<CloudResourceAccess> result = adapter.getCloudResourceAccesses(ids);

        // then
        assertThat(result).containsExactlyInAnyOrder(domain1, domain2);
        verify(repository).findAllById(uuidIterableCaptor.capture());
        List<UUID> passed = iterableToList(uuidIterableCaptor.getValue());
        assertThat(passed).containsExactlyInAnyOrder(id1, id2);
        verify(mapper, times(1)).toDomain(entity1);
        verify(mapper, times(1)).toDomain(entity2);
    }

    @Test
    void findAllById_translatesIds_and_mapsToList_preservingOrder() {
        // given
        Set<CloudResourceAccessId> ids = new LinkedHashSet<>(List.of(CloudResourceAccessId.of(id1), CloudResourceAccessId.of(id2)));
        when(repository.findAllById(any(Iterable.class))).thenReturn(List.of(entity2, entity1)); // repo may return different order
        when(mapper.toDomain(entity2)).thenReturn(domain2);
        when(mapper.toDomain(entity1)).thenReturn(domain1);

        // when
        List<CloudResourceAccess> result = adapter.findAllById(ids);

        // then: adapter preserves repository order after mapping
        assertThat(result).containsExactly(domain2, domain1);
    }

    @Test
    void findAllByCloudClientIdAndResourceType_delegates_and_maps() {
        // given
        CloudAccessClientId clientId = CloudAccessClientId.of("client-A");
        CloudResourceType resourceType = CloudResourceType.of("S3");
        when(repository.findAllByCloudAccessClientIdAndResourceType("client-A", "S3")).thenReturn(Set.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(domain1);

        // when
        Set<CloudResourceAccess> result = adapter.findAllByCloudClientIdAndResourceType(clientId, resourceType);

        // then
        assertThat(result).containsExactly(domain1);
        verify(repository).findAllByCloudAccessClientIdAndResourceType("client-A", "S3");
    }

    @Test
    void findAllByCloudClientId_delegates_and_maps() {
        // given
        CloudAccessClientId clientId = CloudAccessClientId.of("client-B");
        when(repository.findAllByCloudAccessClientId("client-B")).thenReturn(Set.of(entity2));
        when(mapper.toDomain(entity2)).thenReturn(domain2);

        // when
        Set<CloudResourceAccess> result = adapter.findAllByCloudClientId(clientId);

        // then
        assertThat(result).containsExactly(domain2);
        verify(repository).findAllByCloudAccessClientId("client-B");
    }

    @Test
    void findAllByStatus_delegates_and_collectsToMap() {
        // given
        CloudResourcesAccessStatus status = CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE);
        when(repository.findAllByStatus(CloudResourcesAccessStatus.Status.ACTIVE)).thenReturn(Set.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(domain1);

        // when
        Map<CloudResourceAccessId, CloudResourceAccess> result = adapter.findAllByStatus(status);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsEntry(domain1.getCloudResourceAccessId(), domain1);
        verify(repository).findAllByStatus(CloudResourcesAccessStatus.Status.ACTIVE);
    }

    @Test
    void findById_present_and_empty() {
        // present
        when(repository.findById(id1)).thenReturn(Optional.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(domain1);
        assertThat(adapter.findById(CloudResourceAccessId.of(id1))).contains(domain1);

        // empty
        when(repository.findById(id2)).thenReturn(Optional.empty());
        assertThat(adapter.findById(CloudResourceAccessId.of(id2))).isEmpty();
    }

    @Test
    void emptyRepositoryResults_returnEmptyCollections() {
        // given
        when(repository.findAllById(any(Iterable.class))).thenReturn(Collections.emptyList());
        when(repository.findAllByCloudAccessClientIdAndResourceType(anyString(), anyString())).thenReturn(Collections.emptySet());
        when(repository.findAllByCloudAccessClientId(anyString())).thenReturn(Collections.emptySet());
        when(repository.findAllByStatus(any())).thenReturn(Collections.emptySet());

        // when / then
        assertThat(adapter.getCloudResourceAccesses(Set.of())).isEmpty();
        assertThat(adapter.findAllById(Set.of())).isEmpty();
        assertThat(adapter.findAllByCloudClientIdAndResourceType(CloudAccessClientId.of("x"), CloudResourceType.of("y"))).isEmpty();
        assertThat(adapter.findAllByCloudClientId(CloudAccessClientId.of("x"))).isEmpty();
        assertThat(adapter.findAllByStatus(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))).isEmpty();
    }

    // Helpers
    private static CloudResourceAccessEntity entity(UUID id,
                                                    String clientId,
                                                    String resourceType,
                                                    BigDecimal cost,
                                                    BigDecimal used,
                                                    LocalDate expiresAt,
                                                    String cron,
                                                    CloudResourcesAccessStatus.Status status) {
        return CloudResourceAccessEntity.builder()
                .cloudResourceAccessId(id)
                .cloudAccessClientId(clientId)
                .resourceType(resourceType)
                .costLimit(cost)
                .usedLimit(used)
                .expiresAt(expiresAt)
                .cronExpression(cron)
                .status(status)
                .build();
    }

    private static CloudResourceAccess domain(UUID id,
                                              String clientId,
                                              String resourceType,
                                              BigDecimal cost,
                                              BigDecimal used,
                                              LocalDate expiresAt,
                                              String cron,
                                              CloudResourcesAccessStatus.Status status) {
        return CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(id))
                .cloudAccessClientId(CloudAccessClientId.of(clientId))
                .cloudResourceType(CloudResourceType.of(resourceType))
                .costLimit(CostLimit.of(cost))
                .usedLimit(UsedLimit.of(used))
                .expiresAt(expiresAt == null ? com.unicloudapp.management.domain.ExpiresDate.of(java.time.LocalDate.now().plusDays(30)) : com.unicloudapp.management.domain.ExpiresDate.expirable(expiresAt))
                .cronExpression(org.springframework.scheduling.support.CronExpression.parse(cron))
                .status(CloudResourcesAccessStatus.of(status))
                .build();
    }

    private static List<UUID> iterableToList(Iterable<UUID> iterable) {
        List<UUID> list = new ArrayList<>();
        for (UUID uuid : iterable) {
            list.add(uuid);
        }
        return list;
    }
}
