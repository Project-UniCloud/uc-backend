package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudResourceAccess;
import com.unicloudapp.cloudmanagment.domain.CloudResourcesAccessStatus;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CloudResourceAccessRepositoryPort {

    void save(CloudResourceAccess cloudAccess);

    Set<CloudResourceAccess> getCloudResourceAccesses(Set<CloudResourceAccessId> cloudResourceAccessIds);

    List<CloudResourceAccess> findAllById(Set<CloudResourceAccessId> cloudResourceAccessIds);

    Set<CloudResourceAccess> findAllByCloudClientIdAndResourceType(
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType resourceType
    );

    Set<CloudResourceAccess> findAllByCloudClientId(CloudAccessClientId cloudAccessClientId);

    Map<CloudResourceAccessId, CloudResourceAccess> findAllByStatus(CloudResourcesAccessStatus status);

    Optional<CloudResourceAccess> findById(CloudResourceAccessId cloudResourceAccessId);
}
