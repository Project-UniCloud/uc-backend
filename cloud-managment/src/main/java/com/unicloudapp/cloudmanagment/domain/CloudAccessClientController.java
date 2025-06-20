package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;

import java.util.List;

public interface CloudAccessClientController {

    GroupUniqueName createGroup(
            GroupUniqueName groupUniqueName,
            List<UserLogin> lecturerLogins,
            CloudResourceType resourceType
    );

    boolean isRunning();

    boolean isCloudGroupExists(GroupUniqueName groupUniqueName);

    String createUsers(List<String> users, GroupUniqueName groupUniqueName);
}
