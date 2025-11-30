package com.unicloudapp.common.cloud;

import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.group.GroupUniqueName;

import java.util.List;
import java.util.Set;

public interface CloudResourceAccessQueryService {

    Set<CloudResourceType> getCloudResourceTypes(Set<CloudResourceAccessId> cloudResourceAccessIds);

    boolean isCloudGroupExists(GroupUniqueName groupId, CloudAccessClientId CloudAccessClientId);

    List<CloudResourceRowView> getCloudResourceDetails(Set<CloudResourceAccessId> cloudResourceAccesses);

    Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientIdAndResourceType(
            CloudAccessClientId CloudAccessClientId, CloudResourceType resourceType
    );

    Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientId(
            CloudAccessClientId CloudAccessClientId
    );
}
