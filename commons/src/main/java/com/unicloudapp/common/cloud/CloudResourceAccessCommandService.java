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
import java.util.Set;

public interface CloudResourceAccessCommandService {

    CloudResourceAccessId giveGroupCloudResourceAccess(
            CloudAccessClientId CloudAccessClientId,
            CloudResourceType cloudResourceAccessId,
            GroupUniqueName groupUniqueName,
            CostLimit costLimit
    );

    void createGroup(GroupUniqueName groupUniqueName,
                     CloudAccessClientId CloudAccessClientId,
                     List<Map.Entry<UserLogin, Email>> lecturerLogins,
                     CloudResourceType resourceType
    );

    String createUsers(
            CloudAccessClientId CloudAccessClientId,
            List<Map.Entry<UserLogin, Email>> users,
            GroupUniqueName groupUniqueName
    );

    void activateCloudResource(CloudResourceAccessId cloudResourceAccessId);

    void updateGroupCloudResourceAccess(CloudResourceAccessDetailsDto request, GroupUniqueName groupUniqueName);

    void deactivateCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId);

    void cleanUpResources(Set<CloudResourceAccessId> CloudAccessClientId, GroupUniqueName groupUniqueName, boolean force);

    void removeGroup(GroupUniqueName groupUniqueName, CloudAccessClientId CloudAccessClientId);
}
