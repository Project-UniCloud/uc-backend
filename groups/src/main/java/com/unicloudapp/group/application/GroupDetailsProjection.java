package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.GroupStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface GroupDetailsProjection {
    UUID getUuid();
    String getName();
    String getSemester();
    LocalDate getStartDate();
    LocalDate getEndDate();
    GroupStatus.Type getGroupStatus();
    String getDescription();
    Set<UUID> getLecturers();
}
