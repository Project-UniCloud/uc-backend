package com.unicloudapp.common.cloud.event;

import com.unicloudapp.common.vo.user.UserLogin;
import lombok.Builder;

@Builder
public record CloudUserCreatedEvent(
        UserLogin userLogin
) {
}
