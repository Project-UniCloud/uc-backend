package com.unicloudapp.group.application;

import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.group.domain.vo.GroupStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupFilterCriteria {

    private GroupStatus status;
    private GroupName groupName;
    private CloudConnectorId cloudClientId;
    private CloudResourceType resourceType;
    private Boolean pastExpiresDate;
    private UserId lecturerId;
}
