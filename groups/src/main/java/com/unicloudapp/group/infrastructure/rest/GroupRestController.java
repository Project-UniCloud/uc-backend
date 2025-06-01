package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.group.application.*;
import com.unicloudapp.common.domain.group.GroupId;
import com.unicloudapp.group.domain.GroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
class GroupRestController {

    private final GroupService groupService;
    private final StudentImporterPort csvUserImporter;

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
    @PostMapping("/{groupId}/attenders")
    @ResponseStatus(HttpStatus.OK)
    void addAttenders(@PathVariable UUID groupId, @RequestParam("file") MultipartFile file) throws IOException {
        List<StudentBasicData> parsedStudentBasicData = csvUserImporter.parseCsv(file);
        groupService.addAttenders(GroupId.of(groupId), parsedStudentBasicData);
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{groupId}/students")
    Page<UserRowViewResponse> getAttendersDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable UUID groupId
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService.getAttendersDetailsByGroupId(GroupId.of(groupId), pageable)
                .map(userDetails -> UserRowViewResponse.builder()
                        .uuid(userDetails.userId().getValue())
                        .login(userDetails.login().getValue())
                        .firstName(userDetails.firstName().getValue())
                        .lastName(userDetails.lastName().getValue())
                        .email(Objects.requireNonNullElse(userDetails.email(), Email.empty()).getValue())
                        .build()
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{groupId}/cloud-access")
    @ResponseStatus(HttpStatus.CREATED)
    CloudResourceAccessId giveCloudResourceAccess(
            @PathVariable UUID groupId,
            @RequestBody GiveCloudResourceAccessRequest request
    ) {
        return groupService.giveCloudResourceAccess(
                GroupId.of(groupId),
                CloudAccessClientId.of(request.cloudAccessClientId()),
                CloudResourceType.of(request.cloudResourceType())
        );
    }
}
