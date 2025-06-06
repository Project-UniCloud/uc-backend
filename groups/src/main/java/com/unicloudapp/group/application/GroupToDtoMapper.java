package com.unicloudapp.group.application;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.group.Semester;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.Group;
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
    @Mapping(source = "students", target = "students")
    @Mapping(source = "cloudResourceAccesses", target = "cloudResourceAccesses")
    @Mapping(source = "description.value", target = "description")
    GroupDTO toDto(Group group);

    default Set<UUID> toStringSet(Set<CloudResourceAccessId> ids) {
        if (ids == null) return Collections.emptySet();
        return ids.stream()
                .map(CloudResourceAccessId::getValue)
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