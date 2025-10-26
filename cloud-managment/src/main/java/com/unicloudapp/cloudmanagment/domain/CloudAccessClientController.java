package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.cloud.UsedLimit;
import com.unicloudapp.common.domain.user.UserLogin;
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
