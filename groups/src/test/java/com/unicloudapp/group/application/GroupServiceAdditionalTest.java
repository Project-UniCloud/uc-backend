package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GroupServiceAdditionalTest {

    GroupRepositoryPort groupRepository;
    GroupFactory groupFactory;
    CloudResourceAccessQueryService cloudQuery;
    CloudResourceAccessCommandService cloudCmd;
    UserQueryService userQueryService;
    UserCommandService userCmd;

    GroupService service;

    @BeforeEach
    void setUp() {
        groupRepository = mock(GroupRepositoryPort.class);
        groupFactory = mock(GroupFactory.class);
        cloudQuery = mock(CloudResourceAccessQueryService.class);
        cloudCmd = mock(CloudResourceAccessCommandService.class);
        userQueryService = mock(com.unicloudapp.common.user.UserQueryService.class);
        userCmd = mock(com.unicloudapp.common.user.UserCommandService.class);
        service = new GroupService(groupRepository, groupFactory, userQueryService, cloudQuery, cloudCmd, userCmd);
    }

    @Test
    @DisplayName("getCloudResourceAccess: maps to details DTO (happy path)")
    void getCloudResourceAccess_maps_happy() {
        UUID gid = UUID.randomUUID();
        UUID accessUuid = UUID.randomUUID();
        GroupId groupId = GroupId.of(gid);
        CloudResourceAccessId accessId = CloudResourceAccessId.of(accessUuid);

        Group group = mock(Group.class);
        when(groupRepository.findById(gid)).thenReturn(Optional.of(group));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of(accessId));

        CloudResourceRowView row = CloudResourceRowView.builder()
                .id(accessUuid)
                .clientId("clientA")
                .name("S3")
                .costLimit(BigDecimal.TEN)
                .limitUsed(new BigDecimal("3"))
                .expiresAt(LocalDate.of(2025, 12, 31))
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("0 0 * * * *")
                .status("ACTIVE")
                .build();
        when(cloudQuery.getCloudResourceDetails(accessId)).thenReturn(row);

        var dto = service.getCloudResourceAccess(groupId, accessId);
        assertEquals(accessUuid, dto.id());
        assertEquals("0 0 * * * *", dto.cron());
        assertEquals(BigDecimal.TEN, dto.limit());
        assertEquals(LocalDate.of(2025, 12, 31), dto.expiresAt());
    }

    @Test
    @DisplayName("getCloudResourceAccess: group not found throws")
    void getCloudResourceAccess_groupNotFound() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getCloudResourceAccess(gid, CloudResourceAccessId.of(UUID.randomUUID())));
    }

    @Test
    @DisplayName("getCloudResourceAccess: accessId mismatch throws")
    void getCloudResourceAccess_mismatch() {
        UUID gid = UUID.randomUUID();
        GroupId groupId = GroupId.of(gid);
        CloudResourceAccessId requestedId = CloudResourceAccessId.of(UUID.randomUUID());

        Group group = mock(Group.class);
        when(groupRepository.findById(gid)).thenReturn(Optional.of(group));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of(requestedId));

        // Return a row with a DIFFERENT id to trigger mismatch
        CloudResourceRowView row = CloudResourceRowView.builder()
                .id(UUID.randomUUID())
                .clientId("clientA")
                .name("S3")
                .costLimit(BigDecimal.TEN)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .build();
        when(cloudQuery.getCloudResourceDetails(any(CloudResourceAccessId.class))).thenReturn(row);

        assertThrows(RuntimeException.class, () -> service.getCloudResourceAccess(groupId, requestedId));
    }

    @Test
    @DisplayName("saveCloudResourceAccess: delegates to command service with GroupUniqueName")
    void saveCloudResourceAccess_delegates() {
        UUID gid = UUID.randomUUID();
        GroupId groupId = GroupId.of(gid);
        Group group = mock(Group.class);
        when(groupRepository.findById(gid)).thenReturn(Optional.of(group));
        when(group.getSemester()).thenReturn(com.unicloudapp.common.vo.group.Semester.of("2024L"));
        when(group.getName()).thenReturn(com.unicloudapp.common.vo.group.GroupName.of("AI"));

        var req = com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto.builder()
                .id(UUID.randomUUID())
                .cron("0 0 * * * *")
                .limit(new BigDecimal("42"))
                .expiresAt(LocalDate.now().plusDays(5))
                .build();

        service.saveCloudResourceAccess(groupId, req);
        verify(cloudCmd).updateGroupCloudResourceAccess(eq(req), eq(GroupUniqueName.fromString("AI 2024L")));
    }

    @Test
    @DisplayName("saveCloudResourceAccess: group not found throws")
    void saveCloudResourceAccess_groupNotFound() {
        GroupId groupId = GroupId.of(UUID.randomUUID());
        when(groupRepository.findById(groupId.getUuid())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.saveCloudResourceAccess(groupId, com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto.builder().id(UUID.randomUUID()).build()));
    }

    @Test
    @DisplayName("deactivateCloudResourcesAccess: delegates to command service")
    void deactivateCloudResourcesAccess_delegates() {
        UUID gid = UUID.randomUUID();
        GroupId groupId = GroupId.of(gid);
        when(groupRepository.findById(gid)).thenReturn(Optional.of(mock(Group.class)));
        CloudResourceAccessId accessId = CloudResourceAccessId.of(UUID.randomUUID());
        when(cloudQuery.getCloudResourceDetails(Set.of(accessId))).thenReturn(List.of(CloudResourceRowView.builder().clientId("testClientId").build()));

        service.deactivateCloudResourcesAccess(groupId, accessId);
        verify(cloudCmd).deactivateCloudResourceAccess(accessId);
    }

    @Test
    @DisplayName("deactivateCloudResourcesAccess: group not found throws")
    void deactivateCloudResourcesAccess_groupNotFound() {
        GroupId groupId = GroupId.of(UUID.randomUUID());
        when(groupRepository.findById(groupId.getUuid())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.deactivateCloudResourcesAccess(groupId, CloudResourceAccessId.of(UUID.randomUUID())));
    }

    @Test
    @DisplayName("activate: not-found throws")
    void activate_groupNotFound() {
        GroupId groupId = GroupId.of(UUID.randomUUID());
        when(groupRepository.findById(groupId.getUuid())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.activate(groupId));
    }

    @Test
    @DisplayName("archive: not-found throws")
    void archive_groupNotFound() {
        GroupId groupId = GroupId.of(UUID.randomUUID());
        when(groupRepository.findById(groupId.getUuid())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.archive(groupId));
    }
}
