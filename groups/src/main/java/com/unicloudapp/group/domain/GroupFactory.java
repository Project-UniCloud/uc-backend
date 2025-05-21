package com.unicloudapp.group.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GroupFactory {

    public Group create(
            String groupName,
            String semester,
            Set<UUID> lecturers,
            LocalDate startDate,
            LocalDate endDate,
            String description
    ) {
        return Group.builder()
                .groupId(GroupId.of(UUID.randomUUID()))
                .groupStatus(GroupStatus.of(GroupStatus.Type.INACTIVE))
                .lecturers(lecturers.stream()
                        .map(UserId::of)
                        .collect(Collectors.toSet()))
                .attenders(new HashSet<>())
                .cloudAccesses(new HashSet<>())
                .name(GroupName.of(groupName))
                .semester(Semester.of(semester))
                .startDate(StartDate.of(startDate))
                .endDate(EndDate.of(endDate))
                .description(Description.of(description))
                .build();
    }

    public Group restore(UUID groupId,
                         String name,
                         GroupStatus.Type status,
                         String semester,
                         LocalDate startDate,
                         LocalDate endDate,
                         Set<UUID> lecturers,
                         Set<UUID> attenders,
                         Set<String> cloudAccesses,
                         String description
    ) {
        return Group.builder()
                .groupId(GroupId.of(groupId))
                .name(GroupName.of(name))
                .groupStatus(GroupStatus.of(status))
                .semester(Semester.of(semester))
                .startDate(StartDate.of(startDate))
                .endDate(EndDate.of(endDate))
                .lecturers(lecturers.stream()
                        .map(UserId::of)
                        .collect(Collectors.toSet()))
                .attenders(attenders.stream()
                        .map(UserId::of)
                        .collect(Collectors.toSet()))
                .cloudAccesses(cloudAccesses.stream()
                        .map(CloudAccessClientId::of)
                        .collect(Collectors.toSet()))
                .description(Description.of(description))
                .build();
    }
}
