package com.unicloudapp.management.domain;

import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CloudAccessClientController {

    void createGroup(
            GroupUniqueName groupUniqueName,
            List<UserLogin> lecturerLogins,
            CloudResourceType resourceType
    );

    boolean isRunning();

    boolean isCloudGroupExists(GroupUniqueName groupUniqueName);

    String createUsers(List<UserLogin> users, GroupUniqueName groupUniqueName);

    Map<GroupUniqueName, UsedLimit> updateUsedCost(LocalDate startDate, LocalDate endDate);

    void cleanUpResources(GroupUniqueName groupUniqueName, boolean force);
}
