package com.unicloudapp.cloud.domain.connector;

import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.springframework.scheduling.support.CronExpression;

import java.util.List;

@Getter
@Builder
@With
public class CloudConnector {

    private final CloudConnectorId cloudConnectorId;
    private final String host;
    private final Integer port;
    private CostLimit defaultCostLimit;
    private CronExpression cronExpression;
    private String name;
    private final List<CloudResourceType> resourceTypes;

    public boolean containsResourceType(CloudResourceType resourceType) {
        return resourceTypes.contains(resourceType);
    }

    public void syncResourceTypes(List<CloudResourceType> supportedResourceTypes) {
        resourceTypes.clear();
        resourceTypes.addAll(supportedResourceTypes);
    }

    public void updateCostLimit(CostLimit costLimit) {
        defaultCostLimit = costLimit;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCron(CronExpression cronExpression) {
        this.cronExpression = cronExpression;
    }
}
