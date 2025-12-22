package com.unicloudapp.common.user;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.*;
import lombok.Builder;

@Builder
public record UserDetails(
        UserId userId,
        FirstName firstName,
        LastName lastName,
        Email email,
        UserLogin login,
        UserRole roles
) { }
