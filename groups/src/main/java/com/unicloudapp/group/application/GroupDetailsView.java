package com.unicloudapp.group.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GroupDetailsView(
        UUID groupId,
        String name,
        Set<UserFullNameDTO> lecturerFullNames,
        Set<UUID> lecturerIds,
        String semester,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        String status,
        String description) {}
