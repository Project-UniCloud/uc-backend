package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.group.application.GroupDetailsProjection;
import com.unicloudapp.group.application.GroupFilterCriteria;
import com.unicloudapp.group.application.GroupRepositoryPort;
import com.unicloudapp.group.application.GroupRowProjection;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.unicloudapp.group.infrastructure.persistence.GroupSpecifications.*;

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
    public long countByStatus(GroupStatus.Type status) {
        GroupEntity groupEntity = GroupEntity.builder()
                .groupStatus(status)
                .build();
        Example<GroupEntity> example = Example.of(groupEntity);
        return groupJpaRepository.count(example);
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
        if (criteria.getCloudClientId() != null) specs.add(hasCloudClientId(criteria.getCloudClientId()));
        if (criteria.getResourceType() != null) specs.add(hasResourceType(criteria.getResourceType()));

        Specification<GroupEntity> finalSpec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        return groupJpaRepository.findAll(finalSpec, pageable);
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

    Page<GroupRowProjection> findAll(
            Specification<GroupEntity> finalSpec,
            Pageable pageable
    );
}