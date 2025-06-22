package com.unicloudapp.common.cloud;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.cloud.CostLimit;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;

import java.util.List;

public interface CloudResourceAccessCommandService {

    CloudResourceAccessId giveGroupCloudResourceAccess(
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceAccessId,
            GroupUniqueName groupUniqueName,
            CostLimit costLimit
    );

    void createGroup(GroupUniqueName groupUniqueName,
                     CloudAccessClientId cloudAccessClientId,
                     List<UserLogin> lecturerLogins,
                     CloudResourceType resourceType
    );

    String createUsers(
            CloudAccessClientId cloudAccessClientId,
            List<UserLogin> users,
            GroupUniqueName groupUniqueName
    );
}
