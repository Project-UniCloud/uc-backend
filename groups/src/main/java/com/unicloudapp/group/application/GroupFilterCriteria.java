package com.unicloudapp.group.application;

import com.unicloudapp.common.vo.cloud.CloudVendorConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.group.domain.vo.GroupStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupFilterCriteria {

    private GroupStatus status;
    private GroupName groupName;
    private CloudVendorConnectorId cloudClientId;
    private CloudResourceType resourceType;
    private Boolean pastExpiresDate;
}
