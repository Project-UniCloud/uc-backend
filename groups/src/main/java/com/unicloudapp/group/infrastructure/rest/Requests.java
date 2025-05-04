package com.unicloudapp.group.infrastructure.rest;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

record CreateGroupRequest(
        String name,
        String semester,
        Set<UUID> lecturers,
        LocalDate startDate,
        LocalDate endDate
) {

}
