package com.unicloudapp.cloud.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.support.CronExpression;

class SqlCloudConnectorRepositoryAdapterTest {

    private CloudResourceAccessClientJpaRepository jpaRepository;
    private CloudResourceAccessClientMapper mapper;
    private SqlCloudConnectorRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(CloudResourceAccessClientJpaRepository.class);
        mapper = mock(CloudResourceAccessClientMapper.class);
        adapter = new SqlCloudConnectorRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    @DisplayName("save maps domain to entity and delegates to repository")
    void save_delegatesToRepositoryWithMappedEntity() {
        CloudConnector domain = sampleDomain("id-1");
        CloudConnectorEntity entity = sampleEntity("id-1");
        when(mapper.toEntity(domain)).thenReturn(entity);

        adapter.save(domain);

        ArgumentCaptor<CloudConnectorEntity> captor = ArgumentCaptor.forClass(CloudConnectorEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertSame(entity, captor.getValue());
        verify(mapper).toEntity(domain);
    }

    @Test
    @DisplayName("findByClientId returns mapped domain when repository returns entity")
    void findByClientId_present() {
        CloudConnectorId id = CloudConnectorId.of("conn-1");
        CloudConnectorEntity entity = sampleEntity(id.id());
        CloudConnector mapped = sampleDomain(id.id());
        when(jpaRepository.findById(id.id())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(mapped);

        Optional<CloudConnector> result = adapter.findByClientId(id);

        assertTrue(result.isPresent());
        assertSame(mapped, result.get());
        verify(jpaRepository).findById(id.id());
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findByClientId returns empty when repository returns empty")
    void findByClientId_empty() {
        CloudConnectorId id = CloudConnectorId.of("missing");
        when(jpaRepository.findById(id.id())).thenReturn(Optional.empty());

        Optional<CloudConnector> result = adapter.findByClientId(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id.id());
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findAll maps all entities to domains")
    void findAll_list() {
        CloudConnectorEntity e1 = sampleEntity("a");
        CloudConnectorEntity e2 = sampleEntity("b");
        CloudConnector d1 = sampleDomain("a");
        CloudConnector d2 = sampleDomain("b");

        when(jpaRepository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        List<CloudConnector> result = adapter.findAll();

        assertEquals(List.of(d1, d2), result);
        verify(jpaRepository).findAll();
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
    }

    @Test
    @DisplayName("findAll returns empty list when repository empty")
    void findAll_emptyList() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<CloudConnector> result = adapter.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpaRepository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findAll(Pageable) maps page of entities to page of domains preserving metadata")
    void findAll_pageable() {
        Pageable pageable = PageRequest.of(1, 2);
        CloudConnectorEntity e1 = sampleEntity("x");
        CloudConnectorEntity e2 = sampleEntity("y");
        Page<@NotNull CloudConnectorEntity> entityPage = new PageImpl<>(List.of(e1, e2), pageable, 7);

        CloudConnector d1 = sampleDomain("x");
        CloudConnector d2 = sampleDomain("y");

        when(jpaRepository.findAll(pageable)).thenReturn(entityPage);
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        Page<@NotNull CloudConnector> result = adapter.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(List.of(d1, d2), result.getContent());
        assertEquals(7, result.getTotalElements());
        assertEquals(2, result.getSize());
        assertEquals(1, result.getNumber());
        verify(jpaRepository).findAll(pageable);
    }

    private static CloudConnector sampleDomain(String id) {
        return CloudConnector.builder()
                .cloudConnectorId(id != null ? CloudConnectorId.of(id) : null)
                .host("localhost")
                .port(8080)
                .defaultCostLimit(CostLimit.of(new BigDecimal("10")))
                .cronExpression(CronExpression.parse("0 */5 * * * *"))
                .name("name")
                .resourceTypes(new ArrayList<>(List.of(CloudResourceType.of("COMPUTE"))))
                .build();
    }

    private static CloudConnectorEntity sampleEntity(String id) {
        return CloudConnectorEntity.builder()
                .id(id)
                .host("localhost")
                .port(8080)
                .defaultCostLimit(new BigDecimal("10"))
                .defaultCleanUpCron("0 */5 * * * *")
                .name("name")
                .resourceTypes(List.of("COMPUTE"))
                .build();
    }
}
