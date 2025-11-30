package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.cloud.application.port.CloudResourceAccessRepositoryPort;
import com.unicloudapp.cloud.domain.access.CloudResourceAccess;
import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class SqlCloudResourceAccessRepositoryAdapter implements CloudResourceAccessRepositoryPort {

    private final CloudResourceAccessMapper CloudResourceAccessMapper;
    private final CloudResourceAccessJpaRepository repository;

    @Override
    public void save(CloudResourceAccess CloudResourceAccess) {
        repository.save(CloudResourceAccessMapper.toEntity(CloudResourceAccess));
    }

    @Override
    public Set<CloudResourceAccess> getCloudResourceAccesses(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        return repository.findAllById(cloudResourceAccessIds.stream().map(CloudResourceAccessId::getValue).toList())
                .stream()
                .map(CloudResourceAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public List<CloudResourceAccess> findAllById(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        return repository.findAllById(cloudResourceAccessIds.stream().map(CloudResourceAccessId::getValue).toList())
                .stream()
                .map(CloudResourceAccessMapper::toDomain)
                .toList();
    }

    @Override
    public Set<CloudResourceAccess> findAllByCloudClientIdAndResourceType(
            CloudConnectorId cloudConnectorId,
            CloudResourceType resourceType
    ) {
        return repository.findAllByCloudVendorConnectorIdAndResourceType(cloudConnectorId.id(), resourceType.getName())
                .stream()
                .map(CloudResourceAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CloudResourceAccess> findAllByCloudClientId(CloudConnectorId cloudConnectorId) {
        return repository.findAllByCloudVendorConnectorId(cloudConnectorId.id())
                .stream()
                .map(CloudResourceAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<CloudResourceAccessId, CloudResourceAccess> findAllByStatus(CloudResourcesAccessStatus status) {
        return repository.findAllByStatus(status.getStatus())
                .stream()
                .map(CloudResourceAccessMapper::toDomain)
                .collect(Collectors.toMap(
                        CloudResourceAccess::getCloudResourceAccessId,
                        cloudResourceAccess -> cloudResourceAccess
                ));
    }

    @Override
    public Optional<CloudResourceAccess> findById(CloudResourceAccessId cloudResourceAccessId) {
        return repository.findById(cloudResourceAccessId.getValue())
                .map(CloudResourceAccessMapper::toDomain);
    }
}

@Repository
interface CloudResourceAccessJpaRepository extends JpaRepository<CloudResourceAccessEntity, UUID> {

    Set<CloudResourceAccessEntity> findAllByCloudVendorConnectorIdAndResourceType(
            String CloudVendorConnectorId,
            String resourceType
    );

    Set<CloudResourceAccessEntity> findAllByCloudVendorConnectorId(String CloudVendorConnectorId);

    Set<CloudResourceAccessEntity> findAllByStatus(CloudResourcesAccessStatus.Status status);
}
