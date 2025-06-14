package com.unicloudapp.group.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.unicloudapp.common.user.UserFullName;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record GroupDetailsView(
        UUID groupId,
        String name,
        Set<UserFullNameDTO> lecturerFullNames,
        String semester,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
        String status,
        String description
) {

}
