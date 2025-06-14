package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupFilterCriteria {

    private GroupStatus status;
    private GroupName groupName;
    private CloudAccessClientId cloudClientId;
    private CloudResourceType resourceType;
}
