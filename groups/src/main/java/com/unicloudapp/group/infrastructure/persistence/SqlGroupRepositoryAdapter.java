package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupDto;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.group.application.GroupDetailsProjection;
import com.unicloudapp.group.application.GroupFilterCriteria;
import com.unicloudapp.group.application.GroupRowProjection;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.vo.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.unicloudapp.group.infrastructure.persistence.GroupSpecifications.hasCloudResourceAccess;
import static com.unicloudapp.group.infrastructure.persistence.GroupSpecifications.hasPastExpiresDate;
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
        return getGroupRowProjections(criteria, pageable, specs);
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
        return getGroupRowProjections(criteria, pageable, specs);
    }

    private Page<GroupRowProjection> getGroupRowProjections(GroupFilterCriteria criteria, Pageable pageable, List<Specification<GroupEntity>> specs) {
        if (criteria.getPastExpiresDate() != null) {
            specs.add(hasPastExpiresDate());
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
    public List<GroupCloudDto> findActiveGroups() {
        return groupJpaRepository.findAllProjectedByGroupStatus(GroupStatus.Type.ACTIVE)
                .stream()
                .map(groupCloudDtoProjection -> {
                    GroupUniqueName groupUniqueName = GroupUniqueName.fromString(
                            groupCloudDtoProjection.getName() + " " + groupCloudDtoProjection.getSemester()
                    );
                    return new GroupCloudDto(
                            groupUniqueName,
                            groupCloudDtoProjection.getCloudResourceAccesses()
                                    .stream()
                                    .map(CloudResourceAccessId::of)
                                    .collect(Collectors.toList())
                    );
                }).toList();
    }

    @Override
    public GroupDto findByCloudResourceAccessId(CloudResourceAccessId cloudResourceAccessId) {
        return new GroupDto(
                groupJpaRepository.findByCloudResourceAccessesContaining(cloudResourceAccessId.getValue())
                        .getLecturers()
                        .stream()
                        .map(UserId::of)
                        .collect(Collectors.toSet())
        );
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

    List<GroupCloudDtoProjection> findAllProjectedByGroupStatus(GroupStatus.Type groupStatus);

    @EntityGraph(attributePaths = "lecturers")
    GroupEntity findByCloudResourceAccessesContaining(UUID cloudResourceAccessId);
}

interface GroupCloudDtoProjection {

    String getName();
    List<UUID> getCloudResourceAccesses();
    String getSemester();
}