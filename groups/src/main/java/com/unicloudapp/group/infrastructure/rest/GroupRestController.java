package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.domain.GroupId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
class GroupRestController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UUID createGroup(CreateGroupRequest request) {
        GroupDTO groupDto = GroupDTO.builder()
                .name(request.name())
                .semester(request.semester())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .lecturers(request.lecturers())
                .build();
        return groupService.createGroup(groupDto)
                .getGroupId()
                .getUuid();
    }

    @PutMapping("/{groupId}/attenders/{attenderId}")
    @ResponseStatus(HttpStatus.OK)
    void addAttender(@PathVariable UUID groupId, @PathVariable UUID attenderId) {
        groupService.addAttender(GroupId.of(groupId), UserId.of(attenderId));
    }
}
