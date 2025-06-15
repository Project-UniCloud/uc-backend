package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.common.cloud.CloudResourceTypeRowView;
import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.group.GroupId;
import com.unicloudapp.common.domain.group.GroupName;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.group.application.*;
import com.unicloudapp.group.domain.GroupStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    UUID createGroup(@RequestBody @Valid CreateGroupRequest request) {
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
    @PostMapping("/{groupId}/students")
    @ResponseStatus(HttpStatus.OK)
    void addStudent(@PathVariable UUID groupId, @RequestBody @Valid StudentBasicData request) {
        groupService.addStudent(GroupId.of(groupId), request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{groupId}/students/import")
    @ResponseStatus(HttpStatus.OK)
    void importStudents(@PathVariable UUID groupId, @RequestParam("file") MultipartFile file) throws IOException {
        List<StudentBasicData> parsedStudentBasicData = csvUserImporter.parseCsv(file);
        groupService.addStudents(GroupId.of(groupId), parsedStudentBasicData);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    Page<GroupRowView> getAllGroupsByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) GroupStatus.Type status,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String cloudClientId,
            @RequestParam(required = false) String resourceType
    ) {
        if (resourceType != null && cloudClientId == null) {
            throw new IllegalArgumentException("Cloud client id is required when resourceType is given");
        }

        GroupFilterCriteria criteria = GroupFilterCriteria.builder()
                .status(status != null ? GroupStatus.of(status) : null)
                .groupName(groupName != null ? GroupName.of(groupName) : null)
                .cloudClientId(cloudClientId != null ? CloudAccessClientId.of(cloudClientId) : null)
                .resourceType(resourceType != null ? CloudResourceType.of(resourceType) : null)
                .build();

        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService.getGroupsByFilter(criteria, pageable);
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
    Page<UserRowViewResponse> getStudentsDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable @NotNull UUID groupId
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService.getStudentsDetailsByGroupId(GroupId.of(groupId), pageable)
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
            @RequestBody @Valid GiveCloudResourceAccessRequest request
    ) {
        return groupService.giveCloudResourceAccess(
                GroupId.of(groupId),
                CloudAccessClientId.of(request.cloudAccessClientId()),
                CloudResourceType.of(request.cloudResourceType())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    void updateGroup(
            @PathVariable UUID groupId,
            @RequestBody @Valid UpdateGroupDetailsRequest request
    ) {
        groupService.updateGroup(
                GroupId.of(groupId),
                GroupDTO.builder()
                        .groupId(groupId)
                        .name(request.name())
                        .lecturers(request.lecturers())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .description(request.description())
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{groupId}/cloud-access")
    @ResponseStatus(HttpStatus.OK)
    List<CloudResourceTypeRowView> getCloudResourceAccesses(@PathVariable UUID groupId) {
        return groupService.getCloudResourceAccesses(GroupId.of(groupId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{groupId}/activate")
    @ResponseStatus(HttpStatus.OK)
    void activate(@PathVariable UUID groupId) {
        groupService.activate(GroupId.of(groupId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{groupId}/archive")
    @ResponseStatus(HttpStatus.OK)
    void archive(@PathVariable UUID groupId) {
        groupService.archive(GroupId.of(groupId));
    }
}
