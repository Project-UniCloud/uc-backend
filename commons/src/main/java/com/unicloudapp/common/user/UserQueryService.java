package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.user.FullName;
import com.unicloudapp.common.domain.user.UserId;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserQueryService {

    Map<UserId, FullName> getFullNameForUserIds(List<UserId> userIds);

    List<UserDetails> getUserDetailsByIds(Set<UserId> userIds, int offset, int size);

    long countUsersByIds(Set<UserId> userIds);
}
