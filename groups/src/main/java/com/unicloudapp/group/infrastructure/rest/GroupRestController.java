package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceDetail;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupDetailsView;
import com.unicloudapp.group.application.GroupFilterCriteria;
import com.unicloudapp.group.application.GroupRowView;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.application.port.StudentImporterPort;
import com.unicloudapp.group.domain.vo.GroupStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
class GroupRestController {

    private final GroupService groupService;
    private final StudentImporterPort csvUserImporter;
    private final UserQueryService userQueryService;

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
                .description(request.description())
                .build();
        return groupService.createGroup(groupDto).getGroupId().getUuid();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @PostMapping("/{groupId}/students")
    @ResponseStatus(HttpStatus.OK)
    void addStudent(@PathVariable UUID groupId, @RequestBody @Valid StudentBasicData request) {
        checkAccess(groupId);
        groupService.addStudent(GroupId.of(groupId), request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @PostMapping("/{groupId}/students/import")
    @ResponseStatus(HttpStatus.OK)
    void importStudents(@PathVariable UUID groupId, @RequestParam("file") MultipartFile file) throws IOException {
        checkAccess(groupId);
        List<StudentBasicData> parsedStudentBasicData = csvUserImporter.parseCsv(file);
        groupService.addStudents(GroupId.of(groupId), parsedStudentBasicData);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    Page<@org.jetbrains.annotations.NotNull GroupRowView> getAllGroupsByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) GroupStatus.Type status,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String cloudClientId,
            @RequestParam(required = false) String resourceType) {
        if (resourceType != null && cloudClientId == null) {
            throw new IllegalArgumentException("Cloud client id is required when resourceType is given");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserId lecturerId = null;

        if (authentication.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_LECTURER"))
                && authentication.getAuthorities().stream()
                        .noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"))) {
            lecturerId = userQueryService
                    .getUserDetailsByUsername(UserLogin.of(authentication.getName()))
                    .orElseThrow()
                    .userId();
        }

        GroupFilterCriteria criteria = GroupFilterCriteria.builder()
                .status(status != null ? GroupStatus.of(status) : null)
                .groupName(groupName != null ? GroupName.of(groupName) : null)
                .cloudClientId(cloudClientId != null ? CloudConnectorId.of(cloudClientId) : null)
                .resourceType(resourceType != null ? CloudResourceType.of(resourceType) : null)
                .lecturerId(lecturerId)
                .build();

        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService.getGroupsByFilter(criteria, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @GetMapping("/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    GroupDetailsView getGroupById(@PathVariable UUID groupId) {
        checkAccess(groupId);
        return groupService.findById(groupId);
    }

    private void checkAccess(UUID groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_LECTURER"))
                && authentication.getAuthorities().stream()
                        .noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"))) {

            UserId currentUserId = userQueryService
                    .getUserDetailsByUsername(UserLogin.of(authentication.getName()))
                    .orElseThrow()
                    .userId();

            GroupDetailsView group = groupService.findById(groupId);
            if (!group.lecturerIds().contains(currentUserId.getValue())) {
                throw new AccessDeniedException("You don't have access to this group");
            }
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @GetMapping("/{groupId}/students")
    Page<@org.jetbrains.annotations.NotNull UserRowViewResponse> getStudentsDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable @NotNull UUID groupId) {
        checkAccess(groupId);
        Pageable pageable = PageRequest.of(page, pageSize);
        return groupService
                .getStudentsDetailsByGroupId(GroupId.of(groupId), pageable)
                .map(userDetails -> UserRowViewResponse.builder()
                        .uuid(userDetails.userId().getValue())
                        .login(userDetails.login().getValue())
                        .firstName(userDetails.firstName().getValue())
                        .lastName(userDetails.lastName().getValue())
                        .email(Objects.requireNonNullElse(userDetails.email(), Email.empty())
                                .getValue())
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{groupId}/cloud-access")
    @ResponseStatus(HttpStatus.CREATED)
    CloudResourceAccessId giveCloudResourceAccess(
            @PathVariable UUID groupId, @RequestBody @Valid GiveCloudResourceAccessRequest request) {
        return groupService.grantCloudResourceAccess(
                GroupId.of(groupId),
                CloudConnectorId.of(request.cloudConnectorId()),
                CloudResourceType.of(request.cloudResourceType()),
                request.costLimit() == null ? CostLimit.zero() : CostLimit.of(request.costLimit()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    void updateGroup(@PathVariable UUID groupId, @RequestBody @Valid UpdateGroupDetailsRequest request) {
        groupService.updateGroup(
                GroupId.of(groupId),
                GroupDTO.builder()
                        .groupId(groupId)
                        .name(request.name())
                        .lecturers(request.lecturers())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .description(request.description())
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @GetMapping(value = "/{groupId}/cloud-access")
    @ResponseStatus(HttpStatus.OK)
    Page<CloudResourceRowView> getCloudResourceAccesses(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        checkAccess(groupId);
        return groupService.getCloudResourceAccesses(GroupId.of(groupId), PageRequest.of(page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @GetMapping(value = "/{groupId}/cloud-access/{cloudAccessId}")
    @ResponseStatus(HttpStatus.OK)
    CloudResourceAccessDetailsDto getCloudResourceAccesses(
            @PathVariable UUID groupId, @PathVariable UUID cloudAccessId) {
        checkAccess(groupId);
        return groupService.getCloudResourceAccess(GroupId.of(groupId), CloudResourceAccessId.of(cloudAccessId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{groupId}/cloud-access")
    @ResponseStatus(HttpStatus.OK)
    void updateCloudResourceAccesses(@PathVariable UUID groupId, @RequestBody CloudResourceAccessDetailsDto request) {
        groupService.saveCloudResourceAccess(GroupId.of(groupId), request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @PostMapping(value = "/{groupId}/activate")
    @ResponseStatus(HttpStatus.OK)
    void activate(@PathVariable UUID groupId) {
        checkAccess(groupId);
        groupService.activate(GroupId.of(groupId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @PostMapping(value = "/{groupId}/archive")
    @ResponseStatus(HttpStatus.OK)
    void archive(@PathVariable UUID groupId) {
        checkAccess(groupId);
        groupService.archive(GroupId.of(groupId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @PostMapping(value = "/{groupId}/cloud-access/{cloudAccessId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    void deactivateCloudResourcesAccess(@PathVariable UUID groupId, @PathVariable UUID cloudAccessId) {
        checkAccess(groupId);
        groupService.deactivateCloudResourcesAccess(GroupId.of(groupId), CloudResourceAccessId.of(cloudAccessId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @GetMapping(value = "/{groupId}/cloud-access/{cloudAccessId}/resources")
    @ResponseStatus(HttpStatus.OK)
    Page<@org.jetbrains.annotations.NotNull CloudResourceDetail> getGroupResources(
            @PathVariable UUID groupId,
            @PathVariable UUID cloudAccessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        checkAccess(groupId);
        Pageable pageable = PageRequest.of(page, size);
        List<CloudResourceDetail> allResources =
                groupService.getGroupResourcesList(GroupId.of(groupId), CloudResourceAccessId.of(cloudAccessId));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allResources.size());

        if (start > allResources.size()) {
            return new PageImpl<>(List.of(), pageable, allResources.size());
        }

        return new PageImpl<>(allResources.subList(start, end), pageable, allResources.size());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @DeleteMapping(value = "/{groupId}/cloud-access/{cloudAccessId}/resources")
    @ResponseStatus(HttpStatus.OK)
    void deleteResource(
            @PathVariable UUID groupId, @PathVariable UUID cloudAccessId, @RequestParam String resourceGlobalId) {
        checkAccess(groupId);
        groupService.deleteResource(GroupId.of(groupId), CloudResourceAccessId.of(cloudAccessId), resourceGlobalId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @DeleteMapping(value = "/{groupId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteStudentFromGroup(@PathVariable UUID groupId, @PathVariable UUID studentId) {
        checkAccess(groupId);
        groupService.deleteStudentFromGroup(GroupId.of(groupId), UserId.of(studentId));
    }
}
