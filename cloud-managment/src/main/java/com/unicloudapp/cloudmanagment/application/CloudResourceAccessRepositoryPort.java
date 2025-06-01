package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudResourceAccess;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;

import java.util.Set;

public interface CloudResourceAccessRepositoryPort {

    void save(CloudResourceAccess cloudAccess);

    Set<CloudResourceAccess> getCloudResourceAccesses(Set<CloudResourceAccessId> cloudResourceAccessIds);
}
