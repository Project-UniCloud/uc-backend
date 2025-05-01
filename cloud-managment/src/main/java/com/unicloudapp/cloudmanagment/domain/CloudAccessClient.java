package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;
import org.springframework.scheduling.support.CronExpression;

@Getter
@Builder
public class CloudAccessClient {

    private final CloudAccessClientId cloudAccessClientId;
    private final CloudAccessClientController controller;
    private CostLimit costLimit;
    private CronExpression cronExpression;

    public CloudAccess giveCloudAccess(UserId userId,
                                       CloudAccessClientId cloudAccessClientId
    ) {
        return controller.giveCloudAccess(userId, cloudAccessClientId);
    }
}
