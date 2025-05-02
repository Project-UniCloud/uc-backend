package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GroupMapper {

    private final GroupFactory groupFactory;

    GroupEntity toEntity(Group group) {
        return GroupEntity.builder()
                .uuid(group.getGroupId().getUuid())
                .name(group.getName())
                .groupStatus(group.getGroupStatus().getStatus())
                .semester(group.getSemester().toString()) // format: 2024Z
                .startDate(group.getStartDate().getValue())
                .endDate(group.getEndDate().getValue())
                .lecturers(group.getLecturers().stream()
                        .map(UserId::getValue)
                        .toList())
                .attenders(group.getAttenders().stream()
                        .map(UserId::getValue)
                        .toList())
                .cloudAccesses(group.getCloudAccesses().stream()
                        .map(CloudAccessClientId::getValue)
                        .toList())
                .build();
    }

    Group toDomain(GroupEntity groupEntity) {
        return groupFactory.restore(
                groupEntity.getUuid(),
                groupEntity.getName().getName(),
                groupEntity.getGroupStatus(),
                groupEntity.getSemester(),
                groupEntity.getStartDate(),
                groupEntity.getEndDate(),
                groupEntity.getLecturers(),
                groupEntity.getAttenders(),
                groupEntity.getCloudAccesses()
        );
    }
}
