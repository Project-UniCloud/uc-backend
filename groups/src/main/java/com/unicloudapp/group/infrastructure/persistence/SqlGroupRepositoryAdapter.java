package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.group.application.GroupRepositoryPort;
import com.unicloudapp.group.domain.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
class SqlGroupRepositoryAdapter implements GroupRepositoryPort {

    private final GroupJpaRepository groupJpaRepository;
    private final GroupMapper groupMapper;

    @Override
    public Group save(Group group) {
        return groupMapper.toDomain(
                groupJpaRepository.save(
                        groupMapper.toEntity(group)
                )
        );
    }
}

@Repository
interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {

}