package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupDetailsView;
import com.unicloudapp.group.application.GroupRowView;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.domain.GroupId;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
class GroupRestController {

    private final GroupService groupService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UUID createGroup(@RequestBody CreateGroupRequest request) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{groupId}/attenders/{attenderId}")
    @ResponseStatus(HttpStatus.OK)
    void addAttender(@PathVariable UUID groupId, @PathVariable UUID attenderId) {
        groupService.addAttender(GroupId.of(groupId), UserId.of(attenderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<GroupDTO> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService.getAllGroups(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filter")
    Page<GroupRowView> getAllGroupsByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam GroupStatus.Type status
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService.getAllGroupsByStatus(
                pageable, status
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    GroupDetailsView getGroupById(
            @PathVariable UUID groupId
    ) {
        return groupService.findById(groupId);
    }
}
