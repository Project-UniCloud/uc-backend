package com.unicloudapp.management.domain.access_client;

import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.management.domain.access.CloudResourceAccessFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@With
public class CloudResourceAccessClient {

    private final String host;
    private final String port;
    private final CloudAccessClientId CloudAccessClientId;
    private final CloudResourceAccessClientController controller;
    private CostLimit defaultCostLimit;
    private CronExpression cronExpression;
    private String name;
    private List<CloudResourceType> resourceTypes;
    private final CloudResourceAccessFactory CloudResourceAccessFactory;

    public boolean containsResourceType(CloudResourceType resourceType) {
        return resourceTypes.contains(resourceType);
    }

    public void addResourceType(CloudResourceType resourceType) {
        resourceTypes.add(resourceType);
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
