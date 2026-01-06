package com.unicloudapp.user.application.command;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.FirstName;
import com.unicloudapp.common.vo.user.LastName;
import com.unicloudapp.common.vo.user.UserId;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateUserCommand(
        @NotNull UserId userId,
        @NotNull FirstName firstName,
        @NotNull LastName lastName,
        @NotNull Email email) {}
