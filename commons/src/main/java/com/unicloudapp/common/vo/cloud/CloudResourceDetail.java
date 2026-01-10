package com.unicloudapp.common.vo.cloud;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CloudResourceDetail {
    String resourceGlobalId;
    String name;
    String type;
    String service;
    String createdBy;
    String resourceId;
    String status;
}
