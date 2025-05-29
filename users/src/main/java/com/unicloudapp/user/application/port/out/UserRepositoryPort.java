package com.unicloudapp.user.application.port.out;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.user.application.projection.UserFullNameProjection;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);

    boolean existsById(UserId userId);

    boolean existsByLogin(String login);

    List<UserFullNameProjection> findFullNamesByIds(List<UserId> userIds);

    List<UserRowProjection> findUserRowByIds(Collection<UserId> userIds,
                                             int offset,
                                             int size
    );

    List<UserFullNameProjection> searchUserByName(String query, UserRole.Type role);

    long countByUuidIn(Collection<UUID> uuids);

    List<UserId> saveAll(List<User> students);
}
