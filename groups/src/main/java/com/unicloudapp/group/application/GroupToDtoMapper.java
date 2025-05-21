package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
interface GroupToDtoMapper {

    @Mapping(source = "groupId.uuid", target = "groupId")
    @Mapping(source = "name.name", target = "name")
    @Mapping(source = "groupStatus.status", target = "groupStatus")
    @Mapping(source = "semester", target = "semester")
    @Mapping(source = "startDate.value", target = "startDate")
    @Mapping(source = "endDate.value", target = "endDate")
    @Mapping(source = "lecturers", target = "lecturers")
    @Mapping(source = "attenders", target = "attenders")
    @Mapping(source = "cloudAccesses", target = "cloudAccesses")
    @Mapping(source = "description.value", target = "description")
    GroupDTO toDto(Group group);

    default Set<String> toStringSet(Set<CloudAccessClientId> ids) {
        if (ids == null) return Collections.emptySet();
        return ids.stream()
                .map(CloudAccessClientId::getValue)
                .collect(Collectors.toSet());
    }

    default Set<UUID> toCloudAccessClientIdSet(Set<UserId> ids) {
        if (ids == null) return Collections.emptySet();
        return ids.stream().map(UserId::getValue).collect(Collectors.toSet());
    }

    default String map(Semester semester) {
        if (semester == null) return null;
        return semester.toString();
    }
}