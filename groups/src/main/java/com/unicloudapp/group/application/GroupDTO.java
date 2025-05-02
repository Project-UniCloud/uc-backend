package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.GroupStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record GroupDTO(UUID groupId,
                       String name,
                       GroupStatus.Type groupStatus,
                       String semester,
                       LocalDate startDate,
                       LocalDate endDate,
                       List<UUID> lecturers,
                       List<UUID> attenders,
                       List<String> cloudAccesses
) {

}
