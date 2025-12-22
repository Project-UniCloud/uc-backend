package com.unicloudapp.common.user;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;

public interface UserQueryService {

    boolean existsByLogin(String login);

    Map<UserId, UserFullName> getFullNameForUserIds(List<UserId> userIds);

    Page<@NotNull UserDetails> getUserDetailsByIds(Set<UserId> userIds, int pageNumber, int pageSize);

    List<UserLogin> getUserLoginsByIds(Set<UserId> userIds);

    List<Map.Entry<UserLogin, Email>> getUserLoginsAndEmailsByIds(Set<UserId> userIds);

    Optional<UserDetails> getUserDetailsByUsername(UserLogin userLogin);

    List<UserDetails> getAdmins();
}
