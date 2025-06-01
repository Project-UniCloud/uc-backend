package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;

public class CloudResourceAccessFactory {

    public CloudResourceAccess create(
            CloudResourceAccessId cloudResourceAccessId,
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit
    ) {
        if (!isValid(cloudResourceAccessId, cloudAccessClientId, cloudResourceType, costLimit)) {
            throw new IllegalArgumentException("Invalid parameters for creating CloudResourceAccess");
        }
        return new CloudResourceAccess(
                cloudResourceAccessId,
                cloudAccessClientId,
                cloudResourceType,
                costLimit
        );
    }

    private boolean isValid(
            CloudResourceAccessId cloudResourceAccessId,
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit
    ) {
        return cloudResourceAccessId != null &&
                cloudAccessClientId != null &&
                cloudResourceType != null &&
                costLimit != null;
    }
}
