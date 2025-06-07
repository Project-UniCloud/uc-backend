package com.unicloudapp.group.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.unicloudapp.common.validation.GroupName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

record CreateGroupRequest(
        @GroupName String name,
        @NotBlank String semester,
        @NotEmpty Set<UUID> lecturers,
        @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        String description
) { }

record GiveCloudResourceAccessRequest(
        @NotBlank String cloudAccessClientId,
        @NotBlank String cloudResourceType
) {}

record UpdateGroupDetailsRequest(
        @GroupName String name,
        @NotEmpty Set<UUID> lecturers,
        @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        String description
) {}