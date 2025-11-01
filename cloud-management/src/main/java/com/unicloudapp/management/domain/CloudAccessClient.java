package com.unicloudapp.management.domain;

import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
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

    public void createGroup(
            GroupUniqueName groupUniqueName,
            List<UserLogin> lecturerLogins,
            CloudResourceType resourceType
    ) {
        controller.createGroup(groupUniqueName, lecturerLogins, resourceType);
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

    public void cleanUpResources(GroupUniqueName groupUniqueName, boolean force) {
        controller.cleanUpResources(groupUniqueName, force);
    }
}
