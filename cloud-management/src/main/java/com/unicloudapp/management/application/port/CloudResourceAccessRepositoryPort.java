package com.unicloudapp.management.application.port;

import com.unicloudapp.management.domain.access.CloudResourceAccess;
import com.unicloudapp.management.domain.access.CloudResourcesAccessStatus;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CloudResourceAccessRepositoryPort {

    void save(CloudResourceAccess CloudResourceAccess);

    Set<CloudResourceAccess> getCloudResourceAccesses(Set<CloudResourceAccessId> cloudResourceAccessIds);

    List<CloudResourceAccess> findAllById(Set<CloudResourceAccessId> cloudResourceAccessIds);

    Set<CloudResourceAccess> findAllByCloudClientIdAndResourceType(
            CloudAccessClientId CloudAccessClientId,
            CloudResourceType resourceType
    );

    Set<CloudResourceAccess> findAllByCloudClientId(CloudAccessClientId CloudAccessClientId);

    Map<CloudResourceAccessId, CloudResourceAccess> findAllByStatus(CloudResourcesAccessStatus status);

    Optional<CloudResourceAccess> findById(CloudResourceAccessId cloudResourceAccessId);
}
