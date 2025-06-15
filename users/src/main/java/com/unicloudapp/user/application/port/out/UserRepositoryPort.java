package com.unicloudapp.user.application.port.out;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.user.application.projection.UserFullNameProjection;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.*;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);

    boolean existsById(UserId userId);

    boolean existsByLogin(String login);

    List<UserFullNameProjection> findFullNamesByIds(List<UserId> userIds);

    Page<UserRowProjection> findUserRowByIds(Collection<UserId> userIds, int offset, int size);

    List<UserFullNameProjection> searchUserByName(String query, UserRole.Type role);

    List<UserId> saveAll(List<User> students);

    List<UserLogin> findAllLoginsByIds(Set<UserId> userIds);

    Page<UserRowProjection> findAllUsersByRoleAndFirstNameOrLastName(
            int offset,
            int size,
            UserRole.Type role,
            String firstOrLastName
    );
}
