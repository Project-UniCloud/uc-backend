package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessId;
import lombok.Builder;
import lombok.Getter;
import org.springframework.scheduling.support.CronExpression;

@Getter
@Builder
public class CloudAccess {

    private CloudAccessId cloudAccessId;
    private CloudAccessHost host;
    private CloudAccessPort port;
    private CostLimit costLimit;
    private CronExpression cronExpression;
}
