package com.unicloudapp.common.cloud;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.group.GroupUniqueName;

import java.util.List;
import java.util.Set;

public interface CloudResourceAccessQueryService {

    Set<CloudResourceType> getCloudResourceTypes(Set<CloudResourceAccessId> cloudResourceAccessIds);

    boolean isCloudGroupExists(GroupUniqueName groupId, CloudAccessClientId cloudAccessClientId);

    List<CloudResourceTypeRowView> getCloudResourceTypesDetails(Set<CloudResourceAccessId> cloudResourceAccesses);

    Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientIdAndResourceType(
            CloudAccessClientId cloudAccessClientId, CloudResourceType resourceType
    );

    Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientId(
            CloudAccessClientId cloudAccessClientId
    );;
}
