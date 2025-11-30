package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudVendorConnectorId;
import com.unicloudapp.cloud.application.port.CloudResourceAccessClientRepositoryPort;
import com.unicloudapp.cloud.domain.vendor_connector.CloudVendorConnector;
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
    public void save(CloudVendorConnector cloudVendorConnector) {
        repository.save(mapper.toEntity(cloudVendorConnector));
    }

    @Override
    public Optional<CloudVendorConnector> findByClientId(CloudVendorConnectorId clientId) {
        return repository.findById(clientId.id()).map(mapper::toDomain);
    }

    @Override
    public List<CloudVendorConnector> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}

@Repository
interface CloudResourceAccessClientJpaRepository extends JpaRepository<CloudAccessClientEntity, String> {
}