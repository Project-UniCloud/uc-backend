package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserQueryService {

    Map<UserId, UserFullName> getFullNameForUserIds(List<UserId> userIds);

    Page<UserDetails> getUserDetailsByIds(Set<UserId> userIds, int offset, int size);

    List<UserLogin> getUserLoginsByIds(Set<UserId> userIds);
}
