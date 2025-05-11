package com.unicloudapp.cloudmanagment.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record GiveCloudAccessRequest(
        @NotBlank String cloudAccessId,
        @NotNull UUID userId)  {
}
