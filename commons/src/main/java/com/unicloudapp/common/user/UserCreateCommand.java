package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.FirstName;
import com.unicloudapp.common.domain.user.LastName;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import lombok.Builder;

@Builder
public record UserCreateCommand(
        UserLogin userLogin,
        FirstName firstName,
        LastName lastName,
        UserRole userRole,
        Email email
) { }
