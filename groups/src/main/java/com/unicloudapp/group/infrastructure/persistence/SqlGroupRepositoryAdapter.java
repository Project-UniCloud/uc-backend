package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.group.application.GroupDetailsProjection;
import com.unicloudapp.group.application.GroupFilterCriteria;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.application.GroupRowProjection;
import com.unicloudapp.group.domain.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.unicloudapp.group.infrastructure.persistence.GroupSpecifications.hasCloudResourceAccess;
import static com.unicloudapp.group.infrastructure.persistence.GroupSpecifications.hasStatus;
import static com.unicloudapp.group.infrastructure.persistence.GroupSpecifications.nameLike;

@Repository
@RequiredArgsConstructor
class SqlGroupRepositoryAdapter implements GroupRepositoryPort {

    private final GroupJpaRepository groupJpaRepository;
    private final GroupToEntityMapper groupToEntityMapper;

    @Override
    public Group save(Group group) {
        return groupToEntityMapper.toDomain(
                groupJpaRepository.save(
                        groupToEntityMapper.toEntity(group)
                )
        );
    }

    @Override
    public Optional<Group> findById(UUID id) {
        return groupJpaRepository.findById(id)
                .stream()
                .map(groupToEntityMapper::toDomain)
                .findFirst();
    }

    @Override
    public GroupDetailsProjection findGroupDetailsByUuid(UUID uuid) {
        return groupJpaRepository.findGroupDetailsByUuid(uuid);
    }

    @Override
    public boolean existsByNameAndSemester(GroupName name,
                                           Semester semester
    ) {
        return groupJpaRepository.existsByNameAndSemester(name.getName(), semester.toString());
    }

    @Override
    public Page<GroupRowProjection> findAllByCriteria(
            GroupFilterCriteria criteria,
            Pageable pageable
    ) {
        List<Specification<GroupEntity>> specs = new ArrayList<>();

        if (criteria.getStatus() != null) specs.add(hasStatus(criteria.getStatus()));
        if (criteria.getGroupName() != null) specs.add(nameLike(criteria.getGroupName()));

        Specification<GroupEntity> finalSpec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        return groupJpaRepository.findAll(finalSpec, pageable)
                .map(entity -> new GroupRowProjection() {
                    @Override
                    public UUID getUuid() {
                        return entity.getUuid();
                    }

                    @Override
                    public String getName() {
                        return entity.getName();
                    }

                    @Override
                    public String getSemester() {
                        return entity.getSemester();
                    }

                    @Override
                    public LocalDate getEndDate() {
                        return entity.getEndDate();
                    }

                    @Override
                    public Set<UUID> getLecturers() {
                        return entity.getLecturers();
                    }

                    @Override
                    public Set<UUID> getCloudResourceAccesses() {
                        return entity.getCloudResourceAccesses();
                    }
                });
    }

    @Override
    public Page<GroupRowProjection> findAllByCriteriaAndContainsCloudResourceAccess(
            GroupFilterCriteria criteria,
            Pageable pageable,
            Set<CloudResourceAccessId> cloudResourceAccesses
    ) {
        List<Specification<GroupEntity>> specs = new ArrayList<>();

        if (criteria.getStatus() != null) {
            specs.add(hasStatus(criteria.getStatus()));
        }
        if (criteria.getGroupName() != null) {
            specs.add(nameLike(criteria.getGroupName()));
        }
        if (criteria.getCloudClientId() != null || criteria.getResourceType() != null) {
            specs.add(hasCloudResourceAccess(cloudResourceAccesses));
        }

        Specification<GroupEntity> finalSpec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        return groupJpaRepository.findAll(finalSpec, pageable)
                .map(entity -> new GroupRowProjection() {
                    @Override
                    public UUID getUuid() {
                        return entity.getUuid();
                    }

                    @Override
                    public String getName() {
                        return entity.getName();
                    }

                    @Override
                    public String getSemester() {
                        return entity.getSemester();
                    }

                    @Override
                    public LocalDate getEndDate() {
                        return entity.getEndDate();
                    }

                    @Override
                    public Set<UUID> getLecturers() {
                        return entity.getLecturers();
                    }

                    @Override
                    public Set<UUID> getCloudResourceAccesses() {
                        return entity.getCloudResourceAccesses();
                    }
                });
    }

    @Override
    public List<GroupCloudDto> findAllGroupCloudDto() {
        return groupJpaRepository.findAllProjectedBy()
                .stream()
                .map(groupCloudDtoProjection -> {
                    GroupUniqueName groupUniqueName = GroupUniqueName.fromString(
                            groupCloudDtoProjection.getName() + groupCloudDtoProjection.getSemester()
                    );
                    return new GroupCloudDto(
                            groupUniqueName,
                            groupCloudDtoProjection.getCloudResourceAccesses()
                                    .stream()
                                    .map(CloudResourceAccessId::of)
                                    .toList()
                    );
                }).toList();
    }
}

@Repository
interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {

    GroupDetailsProjection findGroupDetailsByUuid(UUID uuid);

    boolean existsByNameAndSemester(
            String name,
            String semester
    );

    boolean existsById(@NonNull UUID uuid);

    Page<GroupEntity> findAll(
            Specification<GroupEntity> finalSpec,
            Pageable pageable
    );

    List<GroupCloudDtoProjection> findAllProjectedBy();
}

interface GroupCloudDtoProjection {

    String getName();
    List<UUID> getCloudResourceAccesses();
    String getSemester();
}