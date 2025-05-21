package com.unicloudapp.group.application;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface GroupRowProjection {

    UUID getUuid();
    String getName();
    String getSemester();
    LocalDate getEndDate();
    Set<UUID> getLecturers();
    Set<String> getCloudAccesses();
}
