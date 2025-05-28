package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.FirstName;
import com.unicloudapp.common.domain.user.LastName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserLogin;
import lombok.Builder;

@Builder
public record UserDetails(
        UserId userId,
        FirstName firstName,
        LastName lastName,
        Email email,
        UserLogin login
) { }
