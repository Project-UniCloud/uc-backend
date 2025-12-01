package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}

@Repository
interface CloudResourceAccessClientJpaRepository extends JpaRepository<CloudConnectorEntity, String> {
}