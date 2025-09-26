package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.application.projection.UserFullNameProjection;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class SqlUserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepositoryJpa userRepositoryJpa;
    private final UserMapper userMapper;
    private final UserFactory userFactory;

    @Override
    public User save(User user) {
        UserEntity entityToSave = userMapper.userToEntity(user);
        UserEntity userEntity = userRepositoryJpa.save(entityToSave);
        return userMapper.entityToUser(userEntity, userFactory);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userRepositoryJpa.findById(userId.getValue())
                .map(foundUser -> userMapper.entityToUser(foundUser, userFactory));
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
    public List<UserFullNameProjection> findFullNamesByIds(List<UserId> userIds) {
        return userRepositoryJpa.findAllByUuidIn(
                userIds.stream()
                        .map(UserId::getValue)
                        .toList()
        );
    }

    @Override
    public Page<UserRowProjection> findUserRowByIds(
            Collection<UserId> userIds,
            int offset,
            int size
    ) {
        Set<UUID> userUUIDs = userIds.stream()
                .map(UserId::getValue)
                .collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(offset / size, size);
        return userRepositoryJpa.getUserEntitiesByUuidIn(userUUIDs, pageable);
    }

    @Override
    public List<UserFullNameProjection> searchUserByName(String query, UserRole.Type role) {
        return userRepositoryJpa.searchUserByName(query, role, PageRequest.of(0, 10));
    }

    @Override
    public List<UserId> saveAll(List<User> students) {
        return userRepositoryJpa.saveAll(students.stream().map(userMapper::userToEntity).toList())
                .stream()
                .map(userEntity -> UserId.of(userEntity.getUuid()))
                .toList();
    }

    @Override
    public List<UserLogin> findAllLoginsByIds(Set<UserId> userIds) {
        return userRepositoryJpa.findAllById(userIds.stream().map(UserId::getValue).collect(Collectors.toSet()))
                .stream()
                .map(entity -> UserLogin.of(entity.getLogin()))
                .toList();
    }

    @Override
    public Page<UserRowProjection> findAllUsersByRoleAndFirstNameOrLastName(
            int offset,
            int size,
            UserRole.Type role,
            String firstOrLastName
    ) {
        if (firstOrLastName == null || firstOrLastName.isBlank()) {
            return userRepositoryJpa.findAllProjectedByRole(
                    role, PageRequest.of(offset / size, size)
            );
        }
        return userRepositoryJpa.findAllByRoleAndFirstNameOrLastNameLike(
                role,
                firstOrLastName,
                PageRequest.of(offset / size, size)
        );
    }

    @Override
    public Optional<User> findByLogin(UserLogin userLogin) {
        return userRepositoryJpa.findByLogin(userLogin.getValue())
                .map(entity -> userMapper.entityToUser(entity, userFactory));
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

    Page<UserRowProjection> getUserEntitiesByUuidIn(Collection<UUID> uuids, Pageable pageable);

    Page<UserRowProjection> findAllProjectedByRole(
            UserRole.Type role,
            Pageable pageable
    );

    @Query("""
        SELECT u
        FROM UserEntity u
        WHERE u.role = :role
            AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstOrLastName, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :firstOrLastName, '%'))
            )
    """)
    Page<UserRowProjection> findAllByRoleAndFirstNameOrLastNameLike(
            UserRole.Type role,
            String firstOrLastName,
            Pageable pageable
    );

    Optional<UserEntity> findByLogin(String login);
}
