package com.unicloudapp.common.cloud;

import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceDetail;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import java.util.List;
import java.util.Set;

public interface CloudResourceAccessQueryService {

    Set<CloudResourceType> getCloudResourceTypes(Set<CloudResourceAccessId> cloudResourceAccessIds);

    boolean isCloudGroupExists(GroupUniqueName groupId, CloudConnectorId cloudConnectorId);

    List<CloudResourceRowView> getCloudResourceDetails(Set<CloudResourceAccessId> cloudResourceAccesses);

    CloudResourceRowView getCloudResourceDetails(CloudResourceAccessId cloudResourceAccess);

    Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientIdAndResourceType(
            CloudConnectorId cloudConnectorId, CloudResourceType resourceType);

    Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientId(CloudConnectorId cloudConnectorId);

    List<CloudResourceDetail> getGroupResourcesList(GroupUniqueName groupUniqueName, CloudConnectorId cloudConnectorId);
}
