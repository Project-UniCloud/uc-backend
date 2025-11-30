package com.unicloudapp.cloud.domain.vendor_connector;

import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CloudVendorClientPort {

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

    void removeGroup(GroupUniqueName groupUniqueName);
}
