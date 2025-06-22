package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import lombok.Builder;
import lombok.Getter;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CloudAccessClient {

    private final CloudAccessClientId cloudAccessClientId;
    private final CloudAccessClientController controller;
    private CostLimit defaultCostLimit;
    private CronExpression cronExpression;
    private String name;
    private List<CloudResourceType> resourceTypes;
    private final CloudResourceAccessFactory cloudResourceAccessFactory;

    public boolean containsResourceType(CloudResourceType resourceType) {
        return resourceTypes.contains(resourceType);
    }

    public GroupUniqueName createGroup(
            GroupUniqueName groupUniqueName,
            List<UserLogin> lecturerLogins,
            CloudResourceType resourceType
    ) {
        return controller.createGroup(groupUniqueName, lecturerLogins, resourceType);
    }

    public boolean isCloudGroupExists(GroupUniqueName groupUniqueName) {
        return controller.isCloudGroupExists(groupUniqueName);
    }

    public String createUsers(List<UserLogin> users, GroupUniqueName groupUniqueName) {
        return controller.createUsers(users, groupUniqueName);
    }

    public Map<GroupUniqueName, UsedLimit> updateUsedCost() {
        return controller.updateUsedCost(LocalDate.EPOCH, LocalDate.now());
    }
}
