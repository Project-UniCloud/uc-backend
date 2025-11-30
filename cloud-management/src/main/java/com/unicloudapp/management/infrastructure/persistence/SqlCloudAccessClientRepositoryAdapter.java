package com.unicloudapp.management.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.management.application.port.CloudResourceAccessClientRepositoryPort;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class SqlCloudResourceAccessClientRepositoryAdapter implements CloudResourceAccessClientRepositoryPort {

    private final CloudResourceAccessClientJpaRepository repository;
    private final CloudResourceAccessClientMapper mapper;

    @Override
    public void save(CloudResourceAccessClient cloudResourceAccessClient) {
        repository.save(mapper.toEntity(cloudResourceAccessClient));
    }

    @Override
    public Optional<CloudResourceAccessClient> findByClientId(CloudAccessClientId clientId) {
        return repository.findById(clientId.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<CloudResourceAccessClient> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}

@Repository
interface CloudResourceAccessClientJpaRepository extends JpaRepository<CloudAccessClientEntity, String> {
}