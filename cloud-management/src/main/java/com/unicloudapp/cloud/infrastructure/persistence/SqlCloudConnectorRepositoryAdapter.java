package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class SqlCloudConnectorRepositoryAdapter implements CloudConnectorRepositoryPort {

    private final CloudResourceAccessClientJpaRepository repository;
    private final CloudResourceAccessClientMapper mapper;

    @Override
    public void save(CloudConnector cloudConnector) {
        repository.save(mapper.toEntity(cloudConnector));
    }

    @Override
    public Optional<CloudConnector> findByClientId(CloudConnectorId clientId) {
        return repository.findById(clientId.id()).map(mapper::toDomain);
    }

    @Override
    public List<CloudConnector> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<@NotNull CloudConnector> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }
}

@Repository
interface CloudResourceAccessClientJpaRepository
        extends JpaRepository<@NotNull CloudConnectorEntity, @NotNull String> {}
