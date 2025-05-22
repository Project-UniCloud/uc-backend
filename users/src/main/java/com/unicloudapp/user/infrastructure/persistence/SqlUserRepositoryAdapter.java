package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.user.application.UserFullNameProjection;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.common.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class SqlUserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepositoryJpa userRepositoryJpa;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserEntity entityToSave = userMapper.userToEntity(user);
        UserEntity userEntity = userRepositoryJpa.save(entityToSave);
        return userMapper.entityToUser(userEntity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userRepositoryJpa.findById(userId.getValue())
                .map(userMapper::entityToUser);
    }

    @Override
    public boolean existsById(UserId userId) {
        return userRepositoryJpa.existsById(userId.getValue());
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepositoryJpa.existsByLogin(login);
    }

    @Override
    public List<UserFullNameProjection> findByIds(List<UserId> userIds) {
        return userRepositoryJpa.findAllByUuidIn(
                userIds.stream()
                        .map(UserId::getValue)
                        .toList()
        );
    }

    @Override
    public List<UserFullNameProjection> searchUserByName(String query, UserRole.Type role) {
        return userRepositoryJpa.searchUserByName(query, role, PageRequest.of(0, 10));
    }
}

@Repository
interface UserRepositoryJpa extends JpaRepository<UserEntity, UUID> {

    boolean existsByLogin(String login);

    List<UserFullNameProjection> findAllByUuidIn(Collection<UUID> uuids);

    @Query("""
       SELECT u.uuid AS uuid, u.firstName AS firstName, u.lastName AS lastName
       FROM UserEntity u
       WHERE (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
       ) AND u.role = :role
       """)
    List<UserFullNameProjection> searchUserByName(String query, UserRole.Type role, Pageable pageable);
}
