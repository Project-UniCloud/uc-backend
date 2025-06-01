package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
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
                .cloudResourceAccesses(group.getCloudResourceAccesses().stream()
                        .map(CloudResourceAccessId::getValue)
                        .collect(Collectors.toSet()))
                .description(group.getDescription().getValue())
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
                groupEntity.getCloudResourceAccesses(),
                groupEntity.getDescription()
        );
    }
}
