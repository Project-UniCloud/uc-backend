package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudVendorConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserFullName;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import com.unicloudapp.group.domain.vo.Description;
import com.unicloudapp.group.domain.vo.EndDate;
import com.unicloudapp.group.domain.vo.GroupStatus;
import com.unicloudapp.group.domain.vo.StartDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceTest {

    GroupRepositoryPort groupRepository;
    GroupFactory groupFactory;
    UserQueryService userQueryService;
    CloudResourceAccessQueryService cloudQuery;
    CloudResourceAccessCommandService cloudCmd;
    UserCommandService userCmd;

    GroupService service;

    @BeforeEach
    void setUp() {
        groupRepository = mock(GroupRepositoryPort.class);
        groupFactory = mock(GroupFactory.class);
        userQueryService = mock(UserQueryService.class);
        cloudQuery = mock(CloudResourceAccessQueryService.class);
        cloudCmd = mock(CloudResourceAccessCommandService.class);
        userCmd = mock(UserCommandService.class);
        service = new GroupService(groupRepository, groupFactory, userQueryService, cloudQuery, cloudCmd, userCmd);
    }

    // createGroup
    @Test
    @DisplayName("createGroup creates via factory and saves; rejects invalid dates and duplicate name+semester")
    void createGroup_behavior() {
        UUID gid = UUID.randomUUID();
        Set<UUID> lecturers = Set.of(UUID.randomUUID());
        GroupDTO dto = GroupDTO.builder()
                .groupId(gid)
                .name("AI")
                .semester("2024L")
                .lecturers(lecturers)
                .startDate(LocalDate.of(2024,1,1))
                .endDate(LocalDate.of(2024,6,30))
                .description("desc")
                .build();

        Group group = mock(Group.class);
        when(groupFactory.create("AI", "2024L", lecturers, dto.startDate(), dto.endDate(), "desc"))
                .thenReturn(group);
        when(groupRepository.existsByNameAndSemester(GroupName.of("AI"), Semester.of("2024L"))).thenReturn(false);
        when(groupRepository.save(group)).thenReturn(group);

        Group created = service.createGroup(dto);
        assertSame(group, created);

        // invalid dates
        GroupDTO badDates = GroupDTO.builder()
                .name("AI")
                .semester("2024L")
                .lecturers(Set.of())
                .startDate(LocalDate.of(2024,6,30))
                .endDate(LocalDate.of(2024,6,30))
                .description("d")
                .build();
        assertThrows(RuntimeException.class, () -> service.createGroup(badDates));

        // duplicate
        when(groupRepository.existsByNameAndSemester(GroupName.of("AI"), Semester.of("2024L"))).thenReturn(true);
        assertThrows(RuntimeException.class, () -> service.createGroup(dto));
    }

    // addStudent existing vs new; ACTIVE triggers cloud createUsers
    @Test
    @DisplayName("addStudent: existing user path; ACTIVE group triggers cloud createUsers and saves")
    void addStudent_existingUser_activeGroup_triggersCloud() {
        GroupId groupId = GroupId.of(UUID.randomUUID());
        StudentBasicData s = StudentBasicData.builder().login("jsmith").firstName("J").lastName("S").email("e@e").build();

        when(userQueryService.existsByLogin("jsmith")).thenReturn(true);
        var details = Optional.of(mock(com.unicloudapp.common.user.UserDetails.class));
        when(details.get().userId()).thenReturn(UserId.of(UUID.randomUUID()));
        when(userQueryService.getUserDetailsByUsername(UserLogin.of("jsmith"))).thenReturn(details);

        Group group = mock(Group.class);
        when(groupRepository.findById(groupId.getUuid())).thenReturn(Optional.of(group));
        when(group.getGroupStatus()).thenReturn(GroupStatus.of(GroupStatus.Type.ACTIVE));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(group.getSemester()).thenReturn(Semester.of("2024L"));
        when(group.getName()).thenReturn(GroupName.of("AI"));

        CloudResourceRowView row1 = CloudResourceRowView.builder()
                .clientId("clientA")
                .name("S3")
                .costLimit(BigDecimal.TEN)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .build();
        when(cloudQuery.getCloudResourceDetails(any())).thenReturn(List.of(row1));

        service.addStudent(groupId, s);

        verify(group).addStudent(any(UserId.class));
        verify(cloudCmd).createUsers(
                eq(CloudVendorConnectorId.of("clientA")),
                eq(List.of(Map.entry(UserLogin.of("jsmith"), Email.empty()))),
                eq(GroupUniqueName.fromString("AI 2024L"))
        );
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("addStudent: new user path; INACTIVE group does not call cloud; saves")
    void addStudent_newUser_inactive_noCloud() {
        GroupId groupId = GroupId.of(UUID.randomUUID());
        StudentBasicData s = StudentBasicData.builder().login("anna").firstName("A").lastName("B").email("a@b").build();

        when(userQueryService.existsByLogin("anna")).thenReturn(false);
        UserId created = UserId.of(UUID.randomUUID());
        when(userCmd.createStudent(s)).thenReturn(created);

        Group group = mock(Group.class);
        when(groupRepository.findById(groupId.getUuid())).thenReturn(Optional.of(group));
        when(group.getGroupStatus()).thenReturn(GroupStatus.of(GroupStatus.Type.INACTIVE));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of());

        service.addStudent(groupId, s);

        verify(group).addStudent(created);
        verify(cloudCmd, never()).createUsers(any(), any(), any());
        verify(groupRepository).save(group);
    }

    // findById mapping
    @Test
    @DisplayName("findById maps projection and user names to view")
    void findById_maps() {
        UUID gid = UUID.randomUUID();
        GroupDetailsProjection proj = new GroupDetailsProjection() {
            @Override public UUID getUuid() { return gid; }
            @Override public String getName() { return "AI"; }
            @Override public String getSemester() { return "2024L"; }
            @Override public LocalDate getStartDate() { return LocalDate.of(2024,1,1); }
            @Override public LocalDate getEndDate() { return LocalDate.of(2024,6,30); }
            @Override public GroupStatus.Type getGroupStatus() { return GroupStatus.Type.ACTIVE; }
            @Override public String getDescription() { return "desc"; }
            @Override public Set<UUID> getLecturers() { return Set.of(UUID.randomUUID(), UUID.randomUUID()); }
        };
        when(groupRepository.findGroupDetailsByUuid(gid)).thenReturn(proj);

        Map<UserId, UserFullName> map = proj.getLecturers().stream()
                .collect(Collectors.toMap(UserId::of, id -> UserFullName.of(UserId.of(id),
                        com.unicloudapp.common.vo.user.FirstName.of("FN"),
                        com.unicloudapp.common.vo.user.LastName.of("LN"))));
        when(userQueryService.getFullNameForUserIds(anyList())).thenReturn(map);

        GroupDetailsView view = service.findById(gid);
        assertEquals(gid, view.groupId());
        assertEquals("AI", view.name());
        assertEquals("2024L", view.semester());
        assertEquals("desc", view.description());
        assertEquals(2, view.lecturerFullNames().size());
    }

    // getStudentsDetailsByGroupId
    @Test
    @DisplayName("getStudentsDetailsByGroupId calculates offset/size and delegates to user service")
    void getStudentsDetailsByGroupId_delegates() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));
        when(group.getStudents()).thenReturn(Set.of(UserId.of(UUID.randomUUID()), UserId.of(UUID.randomUUID())));

        Pageable pageable = PageRequest.of(1, 10); // offset 10
        Page<UserDetails> expected = new PageImpl<>(List.of(mock(UserDetails.class)), pageable, 1);
        when(userQueryService.getUserDetailsByIds(anySet(), eq(10), eq(10))).thenReturn(expected);

        Page<UserDetails> page = service.getStudentsDetailsByGroupId(gid, pageable);
        assertSame(expected, page);
    }

    // addStudents import and possibly cloud
    @Test
    @DisplayName("addStudents imports, adds to group and when ACTIVE creates users in cloud")
    void addStudents_behavior() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        StudentBasicData s1 = StudentBasicData.builder().login("u1").build();
        StudentBasicData s2 = StudentBasicData.builder().login("u2").build();
        List<StudentBasicData> list = List.of(s1, s2);

        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));
        when(group.getGroupStatus()).thenReturn(GroupStatus.of(GroupStatus.Type.ACTIVE));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(group.getSemester()).thenReturn(Semester.of("2024L"));
        when(group.getName()).thenReturn(GroupName.of("AI"));

        UserId id1 = UserId.of(UUID.randomUUID());
        UserId id2 = UserId.of(UUID.randomUUID());
        when(userCmd.importStudents(list)).thenReturn(List.of(id1, id2));

        CloudResourceRowView row = CloudResourceRowView.builder()
                .clientId("clientB")
                .name("EC2")
                .costLimit(BigDecimal.ONE)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .build();
        when(cloudQuery.getCloudResourceDetails(any())).thenReturn(List.of(row));

        service.addStudents(gid, list);

        verify(group, times(1)).addStudent(id1);
        verify(group, times(1)).addStudent(id2);
        verify(cloudCmd).createUsers(eq(CloudVendorConnectorId.of("clientB")), anyList(), eq(GroupUniqueName.fromString("AI 2024L")));
        verify(groupRepository).save(group);
    }

    // giveCloudResourceAccess
    @Test
    @DisplayName("giveCloudResourceAccess prevents duplicates, creates group if missing, gives access and saves")
    void grantCloudResourceAccess_behavior() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));
        when(group.getSemester()).thenReturn(Semester.of("2024L"));
        when(group.getName()).thenReturn(GroupName.of("AI"));
        when(group.getLecturers()).thenReturn(Set.of(UserId.of(UUID.randomUUID())));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of());

        CloudVendorConnectorId clientId = CloudVendorConnectorId.of("clientX");
        CloudResourceType type = CloudResourceType.of("S3");
        CostLimit limit = CostLimit.of(new BigDecimal("5"));

        // no existing access types
        when(cloudQuery.getCloudResourceDetails(any())).thenReturn(List.of());
        when(userQueryService.getUserLoginsByIds(anySet())).thenReturn(List.of(UserLogin.of("lect")));
        when(cloudQuery.isCloudGroupExists(GroupUniqueName.fromString("AI 2024L"), clientId)).thenReturn(false);
        CloudResourceAccessId newId = CloudResourceAccessId.of(UUID.randomUUID());
        when(cloudCmd.giveGroupCloudResourceAccess(clientId, type, GroupUniqueName.fromString("AI 2024L"), limit)).thenReturn(newId);

        CloudResourceAccessId result = service.grantCloudResourceAccess(gid, clientId, type, limit);
        assertEquals(newId, result);
        verify(cloudCmd).createGroup(eq(GroupUniqueName.fromString("AI 2024L")), eq(clientId), anyList(), eq(type));
        verify(group).grantCloudResourceAccess(newId);
        verify(groupRepository).save(group);

        // duplicate path: when details say already has this type/client
        CloudResourceRowView row = CloudResourceRowView.builder()
                .clientId("clientX")
                .name("S3")
                .costLimit(BigDecimal.TEN)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("c")
                .status("ACTIVE")
                .build();
        when(group.getCloudResourceAccesses()).thenReturn(Set.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(cloudQuery.getCloudResourceDetails(any())).thenReturn(List.of(row));
        assertThrows(RuntimeException.class, () -> service.grantCloudResourceAccess(gid, clientId, type, limit));
    }

    @Test
    @DisplayName("getCloudResourceAccesses: delegates to query service")
    void getCloudResourceAccesses_delegates() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));
        Set<CloudResourceAccessId> ids = Set.of(CloudResourceAccessId.of(UUID.randomUUID()));
        when(group.getCloudResourceAccesses()).thenReturn(ids);
        List<CloudResourceRowView> rows = List.of(CloudResourceRowView.builder()
                .clientId("c")
                .name("S3")
                .costLimit(BigDecimal.ZERO)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .build());
        when(cloudQuery.getCloudResourceDetails(ids)).thenReturn(rows);
        assertSame(rows, service.getCloudResourceAccesses(gid));
    }

    @Test
    @DisplayName("updateGroup validates dates, updates domain and saves")
    void updateGroup_behavior() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        GroupDTO dto = GroupDTO.builder()
                .name("AI2")
                .lecturers(Set.of(UUID.randomUUID()))
                .startDate(LocalDate.of(2024,2,1))
                .endDate(LocalDate.of(2024,6,1))
                .description("d")
                .build();

        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));

        service.updateGroup(gid, dto);
        verify(group).update(eq(GroupName.of("AI2")), anySet(), eq(StartDate.of(dto.startDate())), eq(EndDate.of(dto.endDate())), eq(Description.of("d")));
        verify(groupRepository).save(group);

        GroupDTO bad = GroupDTO.builder().name("n").lecturers(Set.of()).startDate(LocalDate.of(2024,6,1)).endDate(LocalDate.of(2024,6,1)).description("d").build();
        assertThrows(RuntimeException.class, () -> service.updateGroup(gid, bad));
    }

    @Test
    @DisplayName("getGroupsByFilter: branches by clientId/resourceType and maps lecturers and access names")
    void getGroupsByFilter_branches_and_maps() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID g1 = UUID.randomUUID();
        Set<UUID> lecturers = Set.of(UUID.randomUUID());
        Set<UUID> accessUuids = Set.of(UUID.randomUUID());
        GroupRowProjection proj = new GroupRowProjection() {
            @Override public UUID getUuid() { return g1; }
            @Override public String getName() { return "AI"; }
            @Override public String getSemester() { return "2024L"; }
            @Override public LocalDate getEndDate() { return LocalDate.of(2024,6,30); }
            @Override public Set<UUID> getLecturers() { return lecturers; }
            @Override public Set<UUID> getCloudResourceAccesses() { return accessUuids; }
        };
        Page<GroupRowProjection> page = new PageImpl<>(List.of(proj), pageable, 1);

        GroupFilterCriteria criteria = GroupFilterCriteria.builder().groupName(GroupName.of("AI")).build();
        when(groupRepository.findAllByCriteria(criteria, pageable)).thenReturn(page);

        Map<UserId, UserFullName> names = Map.of(UserId.of(lecturers.iterator().next()), mock(UserFullName.class));
        names.values().forEach(ufn -> when(ufn.getFullName()).thenReturn("Prof X"));
        when(userQueryService.getFullNameForUserIds(anyList())).thenReturn(names);

        when(cloudQuery.getCloudResourceTypes(accessUuids.stream().map(CloudResourceAccessId::of).collect(Collectors.toSet())))
                .thenReturn(Set.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")));

        Page<GroupRowView> result = service.getGroupsByFilter(criteria, pageable);
        assertEquals(1, result.getContent().size());
        GroupRowView row = result.getContent().getFirst();
        assertEquals("AI", row.name());
        assertTrue(row.CloudResourceAccesses().contains("S3"));
        assertTrue(row.lecturers().contains("Prof X"));

        // With clientId only -> uses getCloudResourceAccessesByCloudClientId then repository.findAllByCriteriaAndContainsCloudResourceAccess
        GroupFilterCriteria withClient = GroupFilterCriteria.builder().cloudClientId(CloudVendorConnectorId.of("client1")).build();
        Set<CloudResourceAccessId> foundIds = Set.of(CloudResourceAccessId.of(UUID.randomUUID()));
        when(cloudQuery.getCloudResourceAccessesByCloudClientId(CloudVendorConnectorId.of("client1"))).thenReturn(foundIds);
        when(groupRepository.findAllByCriteriaAndContainsCloudResourceAccess(withClient, pageable, foundIds)).thenReturn(page);
        service.getGroupsByFilter(withClient, pageable);
        verify(groupRepository).findAllByCriteriaAndContainsCloudResourceAccess(withClient, pageable, foundIds);

        // With clientId + resourceType -> other branch
        GroupFilterCriteria withType = GroupFilterCriteria.builder().cloudClientId(CloudVendorConnectorId.of("client1")).resourceType(CloudResourceType.of("S3")).build();
        when(cloudQuery.getCloudResourceAccessesByCloudClientIdAndResourceType(CloudVendorConnectorId.of("client1"), CloudResourceType.of("S3")))
                .thenReturn(foundIds);
        when(groupRepository.findAllByCriteriaAndContainsCloudResourceAccess(withType, pageable, foundIds)).thenReturn(page);
        service.getGroupsByFilter(withType, pageable);
        verify(groupRepository).findAllByCriteriaAndContainsCloudResourceAccess(withType, pageable, foundIds);
    }

    @Test
    @DisplayName("activate calls domain, creates users in cloud for all clients and saves")
    void activate_behavior() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));
        when(group.getSemester()).thenReturn(Semester.of("2024L"));
        when(group.getName()).thenReturn(GroupName.of("AI"));
        when(group.getCloudResourceAccesses()).thenReturn(Set.of(CloudResourceAccessId.of(UUID.randomUUID())));
        List<Map.Entry<UserLogin, Email>> studentLogins = List.of(
                Map.entry(UserLogin.of("s1"), Email.of("test@example.com")),
                Map.entry(UserLogin.of("s2"), Email.of("test@example.com"))
        );
        when(userQueryService.getUserLoginsAndEmailsByIds(anySet())).thenReturn(studentLogins);
        CloudResourceRowView rowA = CloudResourceRowView.builder()
                .clientId("clientA")
                .name("S3")
                .costLimit(BigDecimal.TEN)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .build();
        CloudResourceRowView rowB = CloudResourceRowView.builder()
                .clientId("clientB")
                .name("EC2")
                .costLimit(BigDecimal.TEN)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .build();
        when(cloudQuery.getCloudResourceDetails(any())).thenReturn(List.of(rowA, rowB));

        service.activate(gid);

        verify(group).activate();
        verify(cloudCmd).createUsers(CloudVendorConnectorId.of("clientA"), studentLogins, GroupUniqueName.fromString("AI 2024L"));
        verify(cloudCmd).createUsers(CloudVendorConnectorId.of("clientB"), studentLogins, GroupUniqueName.fromString("AI 2024L"));
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("archive calls domain and saves")
    void archive_behavior() {
        GroupId gid = GroupId.of(UUID.randomUUID());
        Group group = mock(Group.class);
        when(groupRepository.findById(gid.getUuid())).thenReturn(Optional.of(group));
        service.archive(gid);
        verify(group).archive();
        verify(groupRepository).save(group);
    }
}
