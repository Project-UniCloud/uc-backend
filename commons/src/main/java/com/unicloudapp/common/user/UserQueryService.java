package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.user.FullName;
import com.unicloudapp.common.domain.user.UserId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserQueryService {

    Map<UUID, FullName> getFullNameForUserIds(List<UserId> userIds);
}
