package com.unicloudapp.group.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record GroupDTO(UUID groupId,
                       String name,
                       GroupStatus.Type groupStatus,
                       String semester,
                       @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
                       @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
                       Set<UUID> lecturers,
                       Set<UUID> attenders,
                       Set<String> cloudAccesses,
                       String description
) {

}
