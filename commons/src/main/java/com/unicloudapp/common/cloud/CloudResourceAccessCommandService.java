package com.unicloudapp.common.cloud;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;

import java.util.List;
import java.util.Map;

public interface CloudResourceAccessCommandService {

    CloudResourceAccessId giveGroupCloudResourceAccess(
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceAccessId,
            GroupUniqueName groupUniqueName,
            CostLimit costLimit
    );

    void createGroup(GroupUniqueName groupUniqueName,
                     CloudAccessClientId cloudAccessClientId,
                     List<Map.Entry<UserLogin, Email>> lecturerLogins,
                     CloudResourceType resourceType
    );

    String createUsers(
            CloudAccessClientId cloudAccessClientId,
            List<Map.Entry<UserLogin, Email>> users,
            GroupUniqueName groupUniqueName
    );

    void activateCloudResource(CloudResourceAccessId cloudResourceAccessId);

    void updateGroupCloudResourceAccess(CloudResourceAccessDetailsDto request, GroupUniqueName groupUniqueName);

    void deactivateCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId);
}
