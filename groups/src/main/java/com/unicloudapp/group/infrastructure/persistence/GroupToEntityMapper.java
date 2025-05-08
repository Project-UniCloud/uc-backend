package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.*;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
class GroupToEntityMapper {

    private final GroupFactory groupFactory;

    GroupEntity toEntity(Group group) {
        return GroupEntity.builder()
                .uuid(group.getGroupId().getUuid())
                .name(group.getName().getName())
                .groupStatus(group.getGroupStatus().getStatus())
                .semester(group.getSemester().toString())
                .startDate(group.getStartDate().getValue())
                .endDate(group.getEndDate().getValue())
                .lecturers(group.getLecturers().stream()
                        .map(UserId::getValue)
                        .collect(Collectors.toSet()))
                .attenders(group.getAttenders().stream()
                        .map(UserId::getValue)
                        .collect(Collectors.toSet()))
                .cloudAccesses(group.getCloudAccesses().stream()
                        .map(CloudAccessClientId::getValue)
                        .collect(Collectors.toSet()))
                .build();
    }

    Group toDomain(GroupEntity groupEntity) {
        return groupFactory.restore(
                groupEntity.getUuid(),
                groupEntity.getName(),
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
