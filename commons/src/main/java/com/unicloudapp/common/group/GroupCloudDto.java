package com.unicloudapp.common.group;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;

import java.util.List;

public record GroupCloudDto(
        GroupUniqueName groupUniqueName,
        List<CloudResourceAccessId> cloudResourceAccesses
) { }
