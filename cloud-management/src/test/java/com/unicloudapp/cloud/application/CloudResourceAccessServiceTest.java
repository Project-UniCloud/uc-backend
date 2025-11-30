package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.application.port.CloudResourceAccessRepositoryPort;
import com.unicloudapp.cloud.domain.vo.ExpiresDate;
import com.unicloudapp.cloud.domain.access.CloudResourceAccess;
import com.unicloudapp.cloud.domain.access.CloudResourceAccessFactory;
import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.cloud.application.port.CloudConnectorClientPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.notifications.NotificationsCommandService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.common.vo.user.UserLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CloudResourceAccessServiceTest {

    NotificationsCommandService notificationsCommandService;
    TaskScheduler taskScheduler;
    CloudResourceAccessRepositoryPort repository;
    CloudConnectorRepositoryPort cloudResourceAccessClientRepository;
    GroupQueryService groupQueryService;

    CloudConnectorClientPort controllerA;
    CloudConnectorClientPort controllerB;
    CloudConnector clientA;
    CloudConnector clientB;
    CloudResourceAccessFactory cloudResourceAccessFactory;

    CloudResourceAccessService service;

    @BeforeEach
    void setUp() {
        taskScheduler = mock(TaskScheduler.class);
        repository = mock(CloudResourceAccessRepositoryPort.class);
        cloudResourceAccessClientRepository = mock(CloudConnectorRepositoryPort.class);
        groupQueryService = mock(GroupQueryService.class);
        notificationsCommandService = mock(NotificationsCommandService.class);
        cloudResourceAccessFactory = mock(CloudResourceAccessFactory.class);

        controllerA = mock(CloudConnectorClientPort.class);
        controllerB = mock(CloudConnectorClientPort.class);

        clientA = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .controller(controllerA)
                .name("A")
                .resourceTypes(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")))
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .defaultCostLimit(CostLimit.of(BigDecimal.TEN))
                .build();
        clientB = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("b-client"))
                .controller(controllerB)
                .name("B")
                .resourceTypes(List.of(CloudResourceType.of("S3")))
                .cronExpression(CronExpression.parse("0 */5 * * * *"))
                .defaultCostLimit(CostLimit.of(BigDecimal.ONE))
                .build();

        when(cloudResourceAccessClientRepository.findByClientId(CloudConnectorId.of("b-client")))
                .thenReturn(Optional.ofNullable(clientB));
        when(cloudResourceAccessClientRepository.findByClientId(CloudConnectorId.of("a-client")))
                .thenReturn(Optional.ofNullable(clientA));
        when(cloudResourceAccessClientRepository.findAll()).thenReturn(List.of(clientA, clientB));

        service = new CloudResourceAccessService(taskScheduler, cloudResourceAccessClientRepository, repository, groupQueryService, notificationsCommandService, cloudResourceAccessFactory);
    }

    @Test
    @DisplayName("isCloudClientExists returns true/false")
    void isCloudClientExists() {
        assertTrue(service.isCloudClientExists(CloudConnectorId.of("a-client")));
        assertFalse(service.isCloudClientExists(CloudConnectorId.of("missing")));
    }

    @Test
    @DisplayName("isRunning delegates to controller when client exists; throws when not")
    void isRunning_behavior() {
        when(controllerA.isRunning()).thenReturn(true);
        assertTrue(service.isRunning(CloudConnectorId.of("a-client")));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.isRunning(CloudConnectorId.of("missing")));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("getCloudResourceTypesForCloudResourceAccessClient returns types; throws when client missing")
    void getCloudResourceTypesForCloudResourceAccessClient_behavior() {
        List<CloudResourceType> types = service.getCloudResourceTypesForCloudResourceAccessClient(CloudConnectorId.of("a-client"));
        assertEquals(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")), types);
        assertThrows(IllegalArgumentException.class,
                () -> service.getCloudResourceTypesForCloudResourceAccessClient(CloudConnectorId.of("missing")));
    }

    @Test
    @DisplayName("getCloudResourceTypes maps repository result to set of types")
    void getCloudResourceTypes_maps() {
        CloudResourceAccess cra1 = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        CloudResourceAccess cra2 = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("b-client"))
                .cloudResourceType(CloudResourceType.of("EC2"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(CronExpression.parse("0 */5 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        Set<CloudResourceAccess> set = new HashSet<>(List.of(cra1, cra2));
        when(repository.getCloudResourceAccesses(any())).thenReturn(set);

        Set<CloudResourceType> result = service.getCloudResourceTypes(Set.of(cra1.getCloudResourceAccessId(), cra2.getCloudResourceAccessId()));
        assertEquals(Set.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")), result);
    }

    @Test
    @DisplayName("isCloudGroupExists delegates; throws when client missing")
    void isCloudGroupExists_behavior() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        when(controllerA.isCloudGroupExists(group)).thenReturn(true);
        assertTrue(service.isCloudGroupExists(group, CloudConnectorId.of("a-client")));
        assertThrows(IllegalArgumentException.class,
                () -> service.isCloudGroupExists(group, CloudConnectorId.of("missing")));
    }

    @Test
    @DisplayName("getCloudResourceTypesDetails maps all fields")
    void getCloudResourceDetails_maps() {
        CloudResourceAccess cra = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.of(new BigDecimal("123.45")))
                .usedLimit(UsedLimit.of(new BigDecimal("10")))
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(7)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        when(repository.findAllById(any())).thenReturn(List.of(cra));

        List<CloudResourceRowView> rows = service.getCloudResourceDetails(Set.of(cra.getCloudResourceAccessId()));
        assertEquals(1, rows.size());
        CloudResourceRowView row = rows.getFirst();
        assertEquals("S3", row.name());
        assertEquals(new BigDecimal("123.45"), row.costLimit());
        assertEquals("a-client", row.clientId());
        assertEquals("ACTIVE", row.status());
        assertEquals("0 0 * * * *", row.cronCleanupSchedule());
        assertNotNull(row.lastUsedAt());
        assertEquals(cra.getExpiresAt().getValue(), row.expiresAt());
        assertEquals(new BigDecimal("10"), row.limitUsed());
    }

    @Test
    @DisplayName("getCloudResourceAccesses queries map to ids")
    void getCloudResourceAccesses_queries() {
        CloudResourceAccess cra = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        when(repository.findAllByCloudClientIdAndResourceType(eq(CloudConnectorId.of("a-client")), eq(CloudResourceType.of("S3"))))
                .thenReturn(Set.of(cra));
        when(repository.findAllByCloudClientId(eq(CloudConnectorId.of("a-client"))))
                .thenReturn(Set.of(cra));

        assertEquals(Set.of(cra.getCloudResourceAccessId()),
                service.getCloudResourceAccessesByCloudClientIdAndResourceType(CloudConnectorId.of("a-client"), CloudResourceType.of("S3")));
        assertEquals(Set.of(cra.getCloudResourceAccessId()),
                service.getCloudResourceAccessesByCloudClientId(CloudConnectorId.of("a-client")));
    }

    @Test
    @DisplayName("giveGroupCloudResourceAccess creates, saves, schedules and returns id; errors on missing/unsupported client")
    void giveGroupCloudResourceAccess_behavior() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        CloudResourceType type = CloudResourceType.of("S3");
        CostLimit limit = CostLimit.of(new BigDecimal("50"));

        // Capture scheduling runnable
        ScheduledFuture<?> scheduledFuture = mock(ScheduledFuture.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<CronTrigger> triggerCaptor = ArgumentCaptor.forClass(CronTrigger.class);
        doReturn(scheduledFuture).when(taskScheduler).schedule(runnableCaptor.capture(), triggerCaptor.capture());

        ArgumentCaptor<CloudResourceAccess> savedCaptor = ArgumentCaptor.forClass(CloudResourceAccess.class);
        doNothing().when(repository).save(savedCaptor.capture());
        when(cloudResourceAccessFactory.create(any(), any(), any(), any(), any(), any()))
                .thenReturn(CloudResourceAccess.builder()
                        .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                        .cloudConnectorId(CloudConnectorId.of("a-client"))
                        .cloudResourceType(type)
                        .costLimit(limit)
                        .usedLimit(UsedLimit.empty())
                        .cronExpression(clientA.getCronExpression())
                        .build());

        CloudResourceAccessId returnedId = service.giveGroupCloudResourceAccess(CloudConnectorId.of("a-client"), type, group, limit);

        CloudResourceAccess saved = savedCaptor.getValue();
        assertNotNull(saved);
        assertEquals("a-client", saved.getCloudConnectorId().id());
        assertEquals(type, saved.getCloudResourceType());
        assertEquals(limit, saved.getCostLimit());
        assertEquals(clientA.getCronExpression(), saved.getCronExpression());
        assertEquals(saved.getCloudResourceAccessId(), returnedId);

        // Scheduled trigger based on client's cron
        assertEquals(clientA.getCronExpression().toString(), triggerCaptor.getValue().getExpression());

        // Run the scheduled cleanup and verify controller cleanup
        when(controllerA.isRunning()).thenReturn(true); // not required but present
        runnableCaptor.getValue().run();
        verify(controllerA).cleanUpResources(group, false);

        // Unsupported type
        assertThrows(IllegalArgumentException.class, () ->
                service.giveGroupCloudResourceAccess(CloudConnectorId.of("b-client"), CloudResourceType.of("EC2"), group, limit));
        // Missing client
        assertThrows(IllegalArgumentException.class, () ->
                service.giveGroupCloudResourceAccess(CloudConnectorId.of("missing"), type, group, limit));
    }

    @Test
    @DisplayName("createGroup delegates to client; throws when client missing")
    void createGroup_behavior() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        List<Map.Entry<UserLogin, Email>> lecturers = List.of(Map.entry(UserLogin.of("john"), Email.empty()));
        service.createGroup(group, CloudConnectorId.of("a-client"), lecturers, CloudResourceType.of("EC2"));
        List<UserLogin> lecturerLogins = lecturers.stream()
                .map(Map.Entry::getKey)
                .toList();
        verify(controllerA).createGroup(group, lecturerLogins, CloudResourceType.of("EC2"));
        assertThrows(IllegalArgumentException.class, () ->
                service.createGroup(group, CloudConnectorId.of("missing"), lecturers, CloudResourceType.of("EC2")));
    }

    @Test
    @DisplayName("getCloudResourceAccessClients returns sorted page and details lookup works")
    void clients_listing_and_details() {
        Page<CloudConnector> page = service.getCloudResourceAccessClients(PageRequest.of(0, 10));
        List<CloudConnector> list = page.getContent();
        // Sorted by id string: a-client then b-client
        assertEquals("a-client", list.get(0).getCloudConnectorId().id());
        assertEquals("b-client", list.get(1).getCloudConnectorId().id());
        assertSame(clientA, service.getCloudResourceAccessClientDetails(CloudConnectorId.of("a-client")));
    }

    @Test
    @DisplayName("createUsers delegates to client")
    void createUsers_delegate() {
        List<Map.Entry<UserLogin, Email>> users = List.of(Map.entry(UserLogin.of("u1"), Email.empty()));
        List<UserLogin> logins = users.stream().map(Map.Entry::getKey).toList();
        when(controllerB.createUsers(logins, GroupUniqueName.fromString("AI 2024L"))).thenReturn("ok");
        String res = service.createUsers(CloudConnectorId.of("b-client"), users, GroupUniqueName.fromString("AI 2024L"));
        assertEquals("ok", res);
        verify(controllerB).createUsers(logins, GroupUniqueName.fromString("AI 2024L"));
    }

    @Test
    @DisplayName("updateCostUsed updates used limit and saves changes")
    void updateCostUsed_updatesAndSaves() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        UsedLimit newUsed = UsedLimit.of(new BigDecimal("42"));
        when(controllerA.updateUsedCost(any(), any())).thenReturn(Map.of(group, newUsed));

        CloudResourceAccess cra = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(clientA.getResourceTypes().getFirst())
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        when(repository.findAllByCloudClientIdAndResourceType(clientA.getCloudConnectorId(), clientA.getResourceTypes().getFirst()))
                .thenReturn(Set.of(cra));
        when(repository.findAllById(Set.of(cra.getCloudResourceAccessId()))).thenReturn(List.of(cra));

        service.updateCostUsed();

        verify(repository).save(cra);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("init schedules cleanup tasks for active accesses per group")
    void init_schedulesTasksAndCleanupRuns() {
        // Prepare group dto: one access id
        CloudResourceAccessId accessId = CloudResourceAccessId.of(UUID.randomUUID());
        GroupCloudDto dto = new GroupCloudDto(GroupUniqueName.fromString("AI 2024L"), List.of(accessId));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(dto));

        // Prepare repository active map returning our CloudResourceAccess
        CloudResourceAccess access = CloudResourceAccess.builder()
                .cloudResourceAccessId(accessId)
                .cloudConnectorId(clientA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        when(repository.findAllByStatus(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE)))
                .thenReturn(Map.of(accessId, access));

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        when(taskScheduler.schedule(runnableCaptor.capture(), any(CronTrigger.class)))
                .thenReturn(mock(ScheduledFuture.class));

        service.init();

        // Execute scheduled runnable and verify cleanup
        runnableCaptor.getValue().run();
        verify(controllerA).cleanUpResources(GroupUniqueName.fromString("AI 2024L"), false);
    }

    @Test
    @DisplayName("updateGroupCloudResourceAccess updates entity, reschedules task, and saves")
    void updateGroupCloudResourceAccess_updatesEntityReschedulesAndSaves() {
        // Arrange: create an existing active access and schedule it via init()
        UUID id = UUID.randomUUID();
        CloudResourceAccessId accessId = CloudResourceAccessId.of(id);
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");

        CloudResourceAccess existing = CloudResourceAccess.builder()
                .cloudResourceAccessId(accessId)
                .cloudConnectorId(clientA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.of(new BigDecimal("10")))
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        // init() prerequisites
        GroupCloudDto dto = new GroupCloudDto(group, List.of(accessId));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(dto));
        when(repository.findAllByStatus(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE)))
                .thenReturn(Map.of(accessId, existing));

        // Scheduled future created during init() and re-used for subsequent schedule calls in this test
        ScheduledFuture<?> initialFuture = mock(ScheduledFuture.class);
        doReturn(initialFuture).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        service.init();

        // Prepare update request with new values
        CloudResourceAccessDetailsDto request = CloudResourceAccessDetailsDto.builder()
                .id(id)
                .limit(new BigDecimal("99.99"))
                .cron("0 */15 * * * *")
                .expiresAt(LocalDate.now().plusDays(30))
                .notificationLevel1(50)
                .notificationLevel2(80)
                .notificationLevel3(95)
                .build();

        when(repository.findById(accessId)).thenReturn(Optional.of(existing));

        // Act
        service.updateGroupCloudResourceAccess(request, group);

        // Assert: previous future cancelled and repository saved with updated values
        verify(initialFuture).cancel(anyBoolean());
        ArgumentCaptor<CloudResourceAccess> savedCaptor = ArgumentCaptor.forClass(CloudResourceAccess.class);
        verify(repository, atLeastOnce()).save(savedCaptor.capture());
        CloudResourceAccess saved = savedCaptor.getValue();
        assertEquals(new BigDecimal("99.99"), saved.getCostLimit().getCost());
        assertEquals("0 */15 * * * *", saved.getCronExpression().toString());
        assertEquals(request.expiresAt(), saved.getExpiresAt().getValue());
    }

    @Test
    @DisplayName("deactivateCloudResourceAccess deactivates access, cancels scheduled task, and saves")
    void deactivateCloudResourceAccess_cancelsAndSaves() {
        // Arrange: schedule an existing access via init() so that cancel can work
        CloudResourceAccessId accessId = CloudResourceAccessId.of(UUID.randomUUID());
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");

        CloudResourceAccess existing = CloudResourceAccess.builder()
                .cloudResourceAccessId(accessId)
                .cloudConnectorId(clientA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(7)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        GroupCloudDto dto = new GroupCloudDto(group, List.of(accessId));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(dto));
        when(repository.findAllByStatus(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE)))
                .thenReturn(Map.of(accessId, existing));

        ScheduledFuture<?> initialFuture = mock(ScheduledFuture.class);
        doReturn(initialFuture).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        service.init();

        when(repository.findById(accessId)).thenReturn(Optional.of(existing));

        // Act
        service.deactivateCloudResourceAccess(accessId);

        // Assert
        verify(initialFuture).cancel(anyBoolean());
        ArgumentCaptor<CloudResourceAccess> savedCaptor = ArgumentCaptor.forClass(CloudResourceAccess.class);
        verify(repository).save(savedCaptor.capture());
        CloudResourceAccess saved = savedCaptor.getValue();
        assertEquals("INACTIVE", saved.getStatus().getStatus().name());
    }

    @Test
    @DisplayName("activateCloudResource activates existing access and saves it")
    void activateCloudResource_activatesAndSaves() {
        // Arrange
        CloudResourceAccessId accessId = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccess existing = CloudResourceAccess.builder()
                .cloudResourceAccessId(accessId)
                .cloudConnectorId(clientA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(3)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.INACTIVE))
                .build();

        when(repository.findById(accessId)).thenReturn(Optional.of(existing));

        // Act
        service.activateCloudResource(accessId);

        // Assert
        ArgumentCaptor<CloudResourceAccess> savedCaptor = ArgumentCaptor.forClass(CloudResourceAccess.class);
        verify(repository).save(savedCaptor.capture());
        CloudResourceAccess saved = savedCaptor.getValue();
        assertEquals("ACTIVE", saved.getStatus().getStatus().name());
    }

    @Test
    @DisplayName("activateCloudResource does nothing when access not found")
    void activateCloudResource_notFound_doesNothing() {
        // Arrange
        CloudResourceAccessId accessId = CloudResourceAccessId.of(UUID.randomUUID());
        when(repository.findById(accessId)).thenReturn(Optional.empty());

        // Act
        service.activateCloudResource(accessId);

        // Assert
        verify(repository, org.mockito.Mockito.never()).save(any());
    }


    @Test
    @DisplayName("cleanUpResources loads accesses by ids and calls corresponding client controllers with force=false")
    void cleanUpResources_callsControllers_perAccess_forceFalse() {
        // Arrange
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccessId idB = CloudResourceAccessId.of(UUID.randomUUID());

        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(clientA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(10)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        CloudResourceAccess accessB = CloudResourceAccess.builder()
                .cloudResourceAccessId(idB)
                .cloudConnectorId(clientB.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientB.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(5)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        Set<CloudResourceAccessId> ids = Set.of(idA, idB);
        when(repository.findAllById(ids)).thenReturn(List.of(accessA, accessB));

        // Act
        service.cleanUpResources(ids, group, false);

        // Assert
        verify(repository).findAllById(ids);
        verify(controllerA).cleanUpResources(group, false);
        verify(controllerB).cleanUpResources(group, false);
    }

    @Test
    @DisplayName("cleanUpResources propagates force=true to client controller")
    void cleanUpResources_forceTrue_propagated() {
        // Arrange
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());

        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(clientA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(clientA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(10)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        Set<CloudResourceAccessId> ids = Set.of(idA);
        when(repository.findAllById(ids)).thenReturn(List.of(accessA));

        // Act
        service.cleanUpResources(ids, group, true);

        // Assert
        verify(repository).findAllById(ids);
        verify(controllerA).cleanUpResources(group, true);
    }

    @Test
    @DisplayName("cleanUpResources does nothing when repository returns empty list")
    void cleanUpResources_noAccesses_noControllerCalls() {
        // Arrange
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        Set<CloudResourceAccessId> ids = Set.of(CloudResourceAccessId.of(UUID.randomUUID()));
        when(repository.findAllById(ids)).thenReturn(List.of());

        // Act
        service.cleanUpResources(ids, group, false);

        // Assert
        verify(repository).findAllById(ids);
        org.mockito.Mockito.verifyNoInteractions(controllerA, controllerB);
    }

    @Test
    @DisplayName("removeGroup delegates to controller when client exists")
    void removeGroup_delegates() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        service.removeGroup(group, CloudConnectorId.of("a-client"));
        verify(controllerA).removeGroup(group);
    }

    @Test
    @DisplayName("removeGroup throws when client does not exist")
    void removeGroup_missingClient_throws() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.removeGroup(group, CloudConnectorId.of("missing")));
        assertTrue(ex.getMessage().contains("CloudVendorConnectorId"));
        assertTrue(ex.getMessage().contains("does not exist"));
    }
}