package com.unicloudapp.common.cloud;

import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.scheduling.support.CronExpression;

public interface CloudResourceAccessCommandService {

    CloudResourceAccessId giveGroupCloudResourceAccess(
            CloudConnectorId cloudConnectorId,
            CloudResourceType cloudResourceAccessId,
            GroupUniqueName groupUniqueName,
            CostLimit costLimit);

    void createGroup(
            GroupUniqueName groupUniqueName,
            CloudConnectorId cloudConnectorId,
            List<Map.Entry<UserLogin, Email>> lecturerLogins,
            CloudResourceType resourceType);

    String createUsers(
            CloudConnectorId cloudConnectorId,
            List<Map.Entry<UserLogin, Email>> users,
            GroupUniqueName groupUniqueName);

    void activateCloudResource(CloudResourceAccessId cloudResourceAccessId);

    void updateGroupCloudResourceAccess(CloudResourceAccessDetailsDto request, GroupUniqueName groupUniqueName);

    void deactivateCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId);

    void cleanUpResources(
            Set<CloudResourceAccessId> cloudVendorConnectorId, GroupUniqueName groupUniqueName, boolean force);

    void removeGroup(GroupUniqueName groupUniqueName, CloudConnectorId cloudConnectorId);

    void assignCloudResourceAccess(
            CloudConnectorId cloudConnectorId, GroupUniqueName groupUniqueName, CloudResourceType cloudResourceType);

    void updateCloudResourceAccessClientDetails(
            CloudConnectorId of, CostLimit of1, CronExpression parse, String cloudConnectorName);
}
