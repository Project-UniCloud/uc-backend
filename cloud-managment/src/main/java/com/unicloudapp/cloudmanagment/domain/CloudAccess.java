package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class CloudAccess {

    private final CloudAccessId cloudAccessId;
    private final CloudAccessClientId cloudAccessClientId;
    private final ExternalUserId externalUserId;
    private final UserId userId;

}
