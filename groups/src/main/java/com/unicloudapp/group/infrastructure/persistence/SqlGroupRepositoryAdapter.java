package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.group.application.GroupDetailsProjection;
import com.unicloudapp.group.application.GroupRepositoryPort;
import com.unicloudapp.group.application.GroupRowProjection;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public List<Group> findAll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return groupJpaRepository.findAll(pageable)
                .stream()
                .map(groupToEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return groupJpaRepository.count();
    }

    @Override
    public List<GroupRowProjection> findAllByStatus(int offset,
                                                    int size,
                                                    GroupStatus.Type status
    ) {
        Pageable pageable = PageRequest.of(offset / size, size);
        return groupJpaRepository.findAllByGroupStatus(status, pageable);
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
    public boolean existsById(UUID id) {
        return groupJpaRepository.existsById(id);
    }
}

@Repository
interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {

    List<GroupRowProjection> findAllByGroupStatus(
            GroupStatus.Type groupStatus,
            Pageable pageable
    );

    GroupDetailsProjection findGroupDetailsByUuid(UUID uuid);

    boolean existsByNameAndSemester(
            String name,
            String semester
    );

    boolean existsById(UUID uuid);
}