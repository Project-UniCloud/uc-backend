package com.unicloudapp.common.user;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.FirstName;
import com.unicloudapp.common.vo.user.LastName;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.vo.user.UserRole;
import lombok.Builder;

@Builder
public record UserCreateCommand(
        UserLogin userLogin,
        FirstName firstName,
        LastName lastName,
        UserRole userRole,
        Email email
) { }
