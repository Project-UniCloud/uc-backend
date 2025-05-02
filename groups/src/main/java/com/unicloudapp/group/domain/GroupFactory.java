package com.unicloudapp.group.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupFactory {

    public Group create(
            String groupName,
            String semester,
            List<UUID> lecturers,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return Group.builder()
                .groupId(GroupId.of(UUID.randomUUID()))
                .groupStatus(GroupStatus.of(GroupStatus.Type.INACTIVE))
                .lecturers(lecturers.stream().map(UserId::of).toList())
                .attenders(new ArrayList<>())
                .cloudAccesses(new ArrayList<>())
                .name(GroupName.of(groupName))
                .semester(Semester.of(semester))
                .startDate(StartDate.of(startDate))
                .endDate(EndDate.of(endDate))
                .build();
    }

    public Group restore(UUID groupId,
                         String name,
                         GroupStatus.Type status,
                         String semester,
                         LocalDate startDate,
                         LocalDate endDate,
                         List<UUID> lecturers,
                         List<UUID> attenders,
                         List<String> cloudAccesses) {
        return Group.builder()
                .groupId(GroupId.of(groupId))
                .name(GroupName.of(name))
                .groupStatus(GroupStatus.of(status))
                .semester(Semester.of(semester))
                .startDate(StartDate.of(startDate))
                .endDate(EndDate.of(endDate))
                .lecturers(lecturers.stream()
                        .map(UserId::of)
                        .toList())
                .attenders(attenders.stream()
                        .map(UserId::of)
                        .toList())
                .cloudAccesses(cloudAccesses.stream()
                        .map(CloudAccessClientId::of)
                        .toList())
                .build();
    }
}
