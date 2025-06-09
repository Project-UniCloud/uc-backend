package com.unicloudapp.user.application.port.out;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.user.application.projection.UserFullNameProjection;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;

import java.util.*;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);

    boolean existsById(UserId userId);

    boolean existsByLogin(String login);

    List<UserFullNameProjection> findFullNamesByIds(List<UserId> userIds);

    List<UserRowProjection> findUserRowByIds(Collection<UserId> userIds, int offset, int size);

    List<UserRowProjection> findAllUsers(int offset, int size);

    List<UserFullNameProjection> searchUserByName(String query, UserRole.Type role);

    long countByUuidIn(Collection<UUID> uuids);

    long countAll();

    List<UserId> saveAll(List<User> students);

    List<UserLogin> findAllLoginsByIds(Set<UserId> userIds);
}
