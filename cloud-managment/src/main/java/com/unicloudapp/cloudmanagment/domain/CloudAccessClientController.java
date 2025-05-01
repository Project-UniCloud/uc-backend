package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;

public interface CloudAccessClientController {

    CloudAccess giveCloudAccess(UserId userId,
                                CloudAccessClientId cloudAccessClientId
    );

    boolean isRunning();

    void shutdown();
}
