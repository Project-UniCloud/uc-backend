package com.unicloudapp.cloudmanagment.infrastructure.persistence;

import com.unicloudapp.cloudmanagment.application.CloudAccessRepositoryPort;
import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
class SqlCloudAccessRepositoryAdapter implements CloudAccessRepositoryPort {

    private final CloudAccessMapper cloudAccessMapper;
    private final CloudAccessJpaRepository repository;

    @Override
    public CloudAccess save(CloudAccess cloudAccess) {
        return cloudAccessMapper.toDomain(
                repository.save(
                        cloudAccessMapper.toEntity(cloudAccess)
                )
        );
    }
}

@Repository
interface CloudAccessJpaRepository extends JpaRepository<CloudAccessEntity, UUID> {}
