package com.unicloudapp.group.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

record CreateGroupRequest(
        String name,
        String semester,
        Set<UUID> lecturers,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        String description
) { }

record GiveCloudResourceAccessRequest(
        String cloudAccessClientId,
        String cloudResourceType
) {}

record UpdateGroupDetailsRequest(
        String name,
        Set<UUID> lecturers,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        String description
) {}