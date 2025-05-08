package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.group.application.GroupRepositoryPort;
import com.unicloudapp.group.domain.Group;
import lombok.RequiredArgsConstructor;
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
}

@Repository
interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {

}