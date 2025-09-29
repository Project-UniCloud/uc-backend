package com.unicloudapp.user.application.port.out;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.common.user.UserFullNameAndLoginProjection;
import com.unicloudapp.user.application.projection.UserRowProjection;
import com.unicloudapp.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);

    boolean existsById(UserId userId);

    boolean existsByLogin(String login);

    List<UserFullNameAndLoginProjection> findFullNamesByIds(List<UserId> userIds);

    Page<UserRowProjection> findUserRowByIds(Collection<UserId> userIds, int offset, int size);

    List<UserFullNameAndLoginProjection> searchUserByNameOrLogin(String query, UserRole.Type role);

    List<UserId> saveAll(List<User> students);

    List<UserLogin> findAllLoginsByIds(Set<UserId> userIds);

    Page<UserRowProjection> findAllUsersByRoleAndFirstNameOrLastName(
            int offset,
            int size,
            UserRole.Type role,
            String firstOrLastName
    );

    Optional<User> findByLogin(UserLogin userLogin);
}
