package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.user.application.ports.out.UserRepositoryPort;
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
        return userMapper.entityToUser(userRepositoryJpa.save(userMapper.userToEntity(user)));
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userRepositoryJpa.findById(userId.getValue())
                .map(userMapper::entityToUser);
    }
}

@Repository
interface UserRepositoryJpa extends JpaRepository<UserEntity, UUID> {

}
