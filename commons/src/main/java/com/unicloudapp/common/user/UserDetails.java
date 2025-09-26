package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.*;
import lombok.Builder;

@Builder
public record UserDetails(
        UserId userId,
        FirstName firstName,
        LastName lastName,
        Email email,
        UserLogin login,
        UserRole role
) { }
