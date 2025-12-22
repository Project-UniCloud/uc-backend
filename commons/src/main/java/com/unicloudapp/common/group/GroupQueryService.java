package com.unicloudapp.common.group;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;

import java.util.List;

public interface GroupQueryService {

    List<GroupCloudDto> getActiveGroups();

    GroupDto getGroupByCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId);
}
