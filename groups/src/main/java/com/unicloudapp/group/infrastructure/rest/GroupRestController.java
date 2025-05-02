package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
class GroupRestController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UUID createGroup(CreateGroupRequest request) {
        var groupDto = GroupDTO.builder()
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
}
