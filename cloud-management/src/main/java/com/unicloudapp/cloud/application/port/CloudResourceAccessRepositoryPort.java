package com.unicloudapp.cloud.application.port;

import com.unicloudapp.cloud.domain.access.CloudResourceAccess;
import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
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
            CloudConnectorId cloudConnectorId, CloudResourceType resourceType);

    Set<CloudResourceAccess> findAllByCloudClientId(CloudConnectorId cloudConnectorId);

    Map<CloudResourceAccessId, CloudResourceAccess> findAllByStatus(CloudResourcesAccessStatus status);

    Optional<CloudResourceAccess> findById(CloudResourceAccessId cloudResourceAccessId);
}
