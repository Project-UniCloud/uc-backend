package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.common.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

@Repository
interface UserRepositoryJpa extends JpaRepository<UserEntity, UUID> {

    boolean existsByLogin(String login);
}
