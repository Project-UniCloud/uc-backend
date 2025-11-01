package com.unicloudapp.management.infrastructure.persistence;

import com.unicloudapp.management.application.CloudResourceAccessRepositoryPort;
import com.unicloudapp.management.domain.CloudResourceAccess;
import com.unicloudapp.management.domain.CloudResourcesAccessStatus;
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

    private final CloudAccessMapper cloudAccessMapper;
    private final CloudAccessJpaRepository repository;

    @Override
    public void save(CloudResourceAccess cloudAccess) {
        repository.save(cloudAccessMapper.toEntity(cloudAccess));
    }

    @Override
    public Set<CloudResourceAccess> getCloudResourceAccesses(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        return repository.findAllById(cloudResourceAccessIds.stream().map(CloudResourceAccessId::getValue).toList())
                .stream()
                .map(cloudAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public List<CloudResourceAccess> findAllById(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        return repository.findAllById(cloudResourceAccessIds.stream().map(CloudResourceAccessId::getValue).toList())
                .stream()
                .map(cloudAccessMapper::toDomain)
                .toList();
    }

    @Override
    public Set<CloudResourceAccess> findAllByCloudClientIdAndResourceType(
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType resourceType
    ) {
        return repository.findAllByCloudAccessClientIdAndResourceType(cloudAccessClientId.getValue(), resourceType.getName())
                .stream()
                .map(cloudAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CloudResourceAccess> findAllByCloudClientId(CloudAccessClientId cloudAccessClientId) {
        return repository.findAllByCloudAccessClientId(cloudAccessClientId.getValue())
                .stream()
                .map(cloudAccessMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<CloudResourceAccessId, CloudResourceAccess> findAllByStatus(CloudResourcesAccessStatus status) {
        return repository.findAllByStatus(status.getStatus())
                .stream()
                .map(cloudAccessMapper::toDomain)
                .collect(Collectors.toMap(
                        CloudResourceAccess::getCloudResourceAccessId,
                        cloudResourceAccess -> cloudResourceAccess
                ));
    }

    @Override
    public Optional<CloudResourceAccess> findById(CloudResourceAccessId cloudResourceAccessId) {
        return repository.findById(cloudResourceAccessId.getValue())
                .map(cloudAccessMapper::toDomain);
    }
}

@Repository
interface CloudAccessJpaRepository extends JpaRepository<CloudResourceAccessEntity, UUID> {

    Set<CloudResourceAccessEntity> findAllByCloudAccessClientIdAndResourceType(
            String cloudAccessClientId,
            String resourceType
    );

    Set<CloudResourceAccessEntity> findAllByCloudAccessClientId(String cloudAccessClientId);

    Set<CloudResourceAccessEntity> findAllByStatus(CloudResourcesAccessStatus.Status status);
}
