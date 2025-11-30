package com.unicloudapp.management.infrastructure.persistence;

import com.unicloudapp.management.application.port.CloudResourceAccessRepositoryPort;
import com.unicloudapp.management.domain.access.CloudResourceAccess;
import com.unicloudapp.management.domain.access.CloudResourcesAccessStatus;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
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
            CloudAccessClientId CloudAccessClientId,
            CloudResourceType resourceType
    ) {
        return repository.findAllByCloudAccessClientIdAndResourceType(CloudAccessClientId.getValue(), resourceType.getName())
                .stream()
                .map(CloudResourceAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CloudResourceAccess> findAllByCloudClientId(CloudAccessClientId CloudAccessClientId) {
        return repository.findAllByCloudAccessClientId(CloudAccessClientId.getValue())
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

    Set<CloudResourceAccessEntity> findAllByCloudAccessClientIdAndResourceType(
            String CloudAccessClientId,
            String resourceType
    );

    Set<CloudResourceAccessEntity> findAllByCloudAccessClientId(String CloudAccessClientId);

    Set<CloudResourceAccessEntity> findAllByStatus(CloudResourcesAccessStatus.Status status);
}
