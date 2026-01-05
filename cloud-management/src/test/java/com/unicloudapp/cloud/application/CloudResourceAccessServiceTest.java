package com.unicloudapp.cloud.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unicloudapp.cloud.application.port.CloudConnectorClientFactoryPort;
import com.unicloudapp.cloud.application.port.CloudConnectorClientPort;
import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.application.port.CloudResourceAccessRepositoryPort;
import com.unicloudapp.cloud.domain.access.CloudResourceAccess;
import com.unicloudapp.cloud.domain.access.CloudResourceAccessFactory;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.cloud.domain.vo.ExpiresDate;
import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.cloud.event.CloudUserCreatedEvent;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.notifications.NotificationsCommandService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.NotificationLevel;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

class CloudResourceAccessServiceTest {

    NotificationsCommandService notificationsCommandService;
    TaskScheduler taskScheduler;
    CloudResourceAccessRepositoryPort repository;
    CloudConnectorRepositoryPort cloudConnectorRepositoryPort;
    GroupQueryService groupQueryService;
    ApplicationEventPublisher applicationEventPublisher;

    CloudConnectorClientPort cloudConnectorClientA;
    CloudConnectorClientPort cloudConnectorClientB;
    CloudConnector cloudConnectorA;
    CloudConnector cloudConnectorB;
    CloudResourceAccessFactory cloudResourceAccessFactory;
    CloudConnectorClientFactoryPort cloudControllerClientFactoryPort;

    CloudResourceAccessService service;

    @BeforeEach
    void setUp() {
        taskScheduler = mock(TaskScheduler.class);
        repository = mock(CloudResourceAccessRepositoryPort.class);
        cloudConnectorRepositoryPort = mock(CloudConnectorRepositoryPort.class);
        groupQueryService = mock(GroupQueryService.class);
        notificationsCommandService = mock(NotificationsCommandService.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        cloudResourceAccessFactory = mock(CloudResourceAccessFactory.class);
        cloudControllerClientFactoryPort = mock(CloudConnectorClientFactoryPort.class);

        cloudConnectorClientA = mock(CloudConnectorClientPort.class);
        cloudConnectorClientB = mock(CloudConnectorClientPort.class);

        when(cloudConnectorClientA.getSupportedResourceTypes())
                .thenReturn(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")));
        when(cloudConnectorClientB.getSupportedResourceTypes())
                .thenReturn(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")));

        when(cloudControllerClientFactoryPort.create("localhost", 1234)).thenReturn(cloudConnectorClientA);
        when(cloudControllerClientFactoryPort.create("localhost", 1235)).thenReturn(cloudConnectorClientB);

        cloudConnectorA = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .name("A")
                .host("localhost")
                .port(1234)
                .resourceTypes(new ArrayList<>(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2"))))
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .defaultCostLimit(CostLimit.of(BigDecimal.TEN))
                .build();
        cloudConnectorB = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("b-client"))
                .name("B")
                .host("localhost")
                .port(1235)
                .resourceTypes(new ArrayList<>(List.of(CloudResourceType.of("S3"))))
                .cronExpression(CronExpression.parse("0 */5 * * * *"))
                .defaultCostLimit(CostLimit.of(BigDecimal.ONE))
                .build();

        when(cloudConnectorRepositoryPort.findByClientId(CloudConnectorId.of("b-client")))
                .thenReturn(Optional.ofNullable(cloudConnectorB));
        when(cloudConnectorRepositoryPort.findByClientId(CloudConnectorId.of("a-client")))
                .thenReturn(Optional.ofNullable(cloudConnectorA));
        when(cloudConnectorRepositoryPort.findAll()).thenReturn(List.of(cloudConnectorA, cloudConnectorB));

        service = new CloudResourceAccessService(
                taskScheduler,
                cloudConnectorRepositoryPort,
                repository,
                groupQueryService,
                cloudResourceAccessFactory,
                cloudControllerClientFactoryPort,
                applicationEventPublisher);
        service.init();
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
        when(cloudConnectorClientA.isRunning()).thenReturn(true);
        assertTrue(service.isRunning(CloudConnectorId.of("a-client")));
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.isRunning(CloudConnectorId.of("missing")));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("getCloudResourceTypesForCloudResourceAccessClient returns types; throws when client missing")
    void getCloudResourceTypesForCloudResourceAccessClient_behavior() {
        List<CloudResourceType> types =
                service.getCloudResourceTypesForCloudResourceAccessClient(CloudConnectorId.of("a-client"));
        assertThat(types).containsExactlyInAnyOrder(CloudResourceType.of("S3"), CloudResourceType.of("EC2"));
        assertThrows(
                IllegalArgumentException.class,
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

        Set<CloudResourceType> result =
                service.getCloudResourceTypes(Set.of(cra1.getCloudResourceAccessId(), cra2.getCloudResourceAccessId()));
        assertEquals(Set.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")), result);
    }

    @Test
    @DisplayName("isCloudGroupExists delegates; throws when client missing")
    void isCloudGroupExists_behavior() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        when(cloudConnectorClientA.isCloudGroupExists(group)).thenReturn(true);
        assertTrue(service.isCloudGroupExists(group, CloudConnectorId.of("a-client")));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.isCloudGroupExists(group, CloudConnectorId.of("missing")));
    }

    @Test
    @DisplayName("getCloudResourceDetails (single) maps all fields")
    void getCloudResourceDetails_single_maps() {
        CloudResourceAccess cra = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.of(new BigDecimal("123.45")))
                .usedLimit(UsedLimit.of(new BigDecimal("10")))
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(7)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .notificationLevel1(NotificationLevel.of(1))
                .notificationLevel2(NotificationLevel.of(2))
                .notificationLevel3(NotificationLevel.of(3))
                .build();
        when(repository.findById(cra.getCloudResourceAccessId())).thenReturn(Optional.of(cra));

        CloudResourceRowView row = service.getCloudResourceDetails(cra.getCloudResourceAccessId());
        assertEquals(cra.getCloudResourceAccessId().getValue(), row.id());
        assertEquals("S3", row.name());
        assertEquals(new BigDecimal("123.45"), row.costLimit());
        assertEquals("a-client", row.clientId());
        assertEquals("ACTIVE", row.status());
        assertEquals("0 0 * * * *", row.cronCleanupSchedule());
        assertNotNull(row.lastUsedAt());
        assertEquals(cra.getExpiresAt().getValue(), row.expiresAt());
        assertEquals(new BigDecimal("10"), row.limitUsed());
        assertEquals(1, row.notificationLevel1());
        assertEquals(2, row.notificationLevel2());
        assertEquals(3, row.notificationLevel3());
    }

    @Test
    @DisplayName("getCloudResourceDetails (single) throws when not found")
    void getCloudResourceDetails_single_notFound() {
        CloudResourceAccessId id = CloudResourceAccessId.of(UUID.randomUUID());
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> service.getCloudResourceDetails(id));
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
                .notificationLevel1(NotificationLevel.of(1))
                .notificationLevel2(NotificationLevel.of(2))
                .notificationLevel3(NotificationLevel.of(3))
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
        assertEquals(1, row.notificationLevel1());
        assertEquals(2, row.notificationLevel2());
        assertEquals(3, row.notificationLevel3());
    }

    @Test
    @DisplayName("getCloudResourceDetails (multiple) handles empty and multiple")
    void getCloudResourceDetails_set_behavior() {
        // Empty
        when(repository.findAllById(anySet())).thenReturn(List.of());
        assertTrue(service.getCloudResourceDetails(Set.of()).isEmpty());

        // Multiple
        CloudResourceAccess cra1 = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now()))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .notificationLevel1(NotificationLevel.of(1))
                .notificationLevel2(NotificationLevel.of(2))
                .notificationLevel3(NotificationLevel.of(3))
                .build();
        CloudResourceAccess cra2 = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("b-client"))
                .cloudResourceType(CloudResourceType.of("EC2"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now()))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .notificationLevel1(NotificationLevel.of(1))
                .notificationLevel2(NotificationLevel.of(2))
                .notificationLevel3(NotificationLevel.of(3))
                .build();

        when(repository.findAllById(anySet())).thenReturn(List.of(cra1, cra2));
        List<CloudResourceRowView> results = service.getCloudResourceDetails(
                Set.of(cra1.getCloudResourceAccessId(), cra2.getCloudResourceAccessId()));
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.name().equals("S3")));
        assertTrue(results.stream().anyMatch(r -> r.name().equals("EC2")));
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
        when(repository.findAllByCloudClientIdAndResourceType(
                        eq(CloudConnectorId.of("a-client")), eq(CloudResourceType.of("S3"))))
                .thenReturn(Set.of(cra));
        when(repository.findAllByCloudClientId(eq(CloudConnectorId.of("a-client"))))
                .thenReturn(Set.of(cra));

        assertEquals(
                Set.of(cra.getCloudResourceAccessId()),
                service.getCloudResourceAccessesByCloudClientIdAndResourceType(
                        CloudConnectorId.of("a-client"), CloudResourceType.of("S3")));
        assertEquals(
                Set.of(cra.getCloudResourceAccessId()),
                service.getCloudResourceAccessesByCloudClientId(CloudConnectorId.of("a-client")));
    }

    @Test
    @DisplayName(
            "giveGroupCloudResourceAccess creates, saves, schedules and returns id; errors on missing/unsupported client")
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
                        .cronExpression(cloudConnectorA.getCronExpression())
                        .build());

        CloudResourceAccessId returnedId =
                service.giveGroupCloudResourceAccess(CloudConnectorId.of("a-client"), type, group, limit);

        CloudResourceAccess saved = savedCaptor.getValue();
        assertNotNull(saved);
        assertEquals("a-client", saved.getCloudConnectorId().id());
        assertEquals(type, saved.getCloudResourceType());
        assertEquals(limit, saved.getCostLimit());
        assertEquals(cloudConnectorA.getCronExpression(), saved.getCronExpression());
        assertEquals(saved.getCloudResourceAccessId(), returnedId);

        // Scheduled trigger based on client's cron
        assertEquals(
                cloudConnectorA.getCronExpression().toString(),
                triggerCaptor.getValue().getExpression());

        // Run the scheduled cleanup and verify controller cleanup
        when(cloudConnectorClientA.isRunning()).thenReturn(true); // not required but present
        runnableCaptor.getValue().run();
        verify(cloudConnectorClientA).cleanUpResources(group, false);

        // Unsupported type
        assertThrows(
                IllegalArgumentException.class,
                () -> service.giveGroupCloudResourceAccess(
                        CloudConnectorId.of("b-client"), CloudResourceType.of("DynamoDB"), group, limit));
        // Missing client
        assertThrows(
                IllegalArgumentException.class,
                () -> service.giveGroupCloudResourceAccess(CloudConnectorId.of("missing"), type, group, limit));
    }

    @Test
    @DisplayName("createGroup delegates to client and publishes events; throws when client missing")
    void createGroup_behavior() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        List<Map.Entry<UserLogin, Email>> lecturers = List.of(Map.entry(UserLogin.of("john"), Email.empty()));
        service.createGroup(group, CloudConnectorId.of("a-client"), lecturers, CloudResourceType.of("EC2"));
        List<UserLogin> lecturerLogins =
                lecturers.stream().map(Map.Entry::getKey).toList();
        verify(cloudConnectorClientA).createGroup(group, lecturerLogins, CloudResourceType.of("EC2"));
        verify(applicationEventPublisher, times(lecturers.size())).publishEvent(any(CloudUserCreatedEvent.class));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.createGroup(
                        group, CloudConnectorId.of("missing"), lecturers, CloudResourceType.of("EC2")));
    }

    @Test
    @DisplayName("getCloudResourceAccessClients returns sorted page and details lookup works")
    void clients_listing_and_details() {
        when(cloudConnectorRepositoryPort.findAll(any()))
                .thenReturn(new PageImpl<>(List.of(cloudConnectorA, cloudConnectorB), PageRequest.of(0, 10), 2));
        Page<@NotNull CloudConnector> page = service.getCloudResourceAccessClients(PageRequest.of(0, 10));
        List<CloudConnector> list = page.getContent();
        // Sorted by id string: a-client then b-client
        assertEquals("a-client", list.get(0).getCloudConnectorId().id());
        assertEquals("b-client", list.get(1).getCloudConnectorId().id());
        assertSame(cloudConnectorA, service.getCloudResourceAccessClientDetails(CloudConnectorId.of("a-client")));
    }

    @Test
    @DisplayName("createUsers delegates to client and publishes events")
    void createUsers_delegate() {
        Set<UserLogin> users = Set.of(UserLogin.of("u1"));
        List<UserLogin> logins = users.stream().toList();
        when(cloudConnectorClientB.createUsers(logins, GroupUniqueName.fromString("AI 2024L")))
                .thenReturn("ok");
        String res =
                service.createUsers(CloudConnectorId.of("b-client"), users, GroupUniqueName.fromString("AI 2024L"));
        assertEquals("ok", res);
        verify(cloudConnectorClientB).createUsers(logins, GroupUniqueName.fromString("AI 2024L"));
        verify(applicationEventPublisher, times(users.size())).publishEvent(any(CloudUserCreatedEvent.class));
    }

    @Test
    @DisplayName("removeUsers delegates to client for each user")
    void removeUsers_delegate() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        UserLogin u1 = UserLogin.of("u1");
        UserLogin u2 = UserLogin.of("u2");
        Set<UserLogin> users = Set.of(u1, u2);

        service.removeUsers(CloudConnectorId.of("a-client"), users, group);

        verify(cloudConnectorClientA).removeUser(u1, group);
        verify(cloudConnectorClientA).removeUser(u2, group);
        verify(cloudConnectorClientA, times(2)).removeUser(any(), eq(group));
    }

    @Test
    @DisplayName("updateCostUsed updates used limit and saves changes")
    void updateCostUsed_updatesAndSaves() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        UsedLimit newUsed = UsedLimit.of(new BigDecimal("42"));
        when(cloudConnectorClientA.updateUsedCost(any(), any(), eq(group))).thenReturn(newUsed);

        CloudResourceAccess cra = CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(UUID.randomUUID()))
                .cloudConnectorId(CloudConnectorId.of("a-client"))
                .cloudResourceType(cloudConnectorA.getResourceTypes().getFirst())
                .costLimit(CostLimit.of(new BigDecimal("100")))
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .notificationLevel1(NotificationLevel.of(50))
                .notificationLevel2(NotificationLevel.of(80))
                .notificationLevel3(NotificationLevel.of(100))
                .build();

        GroupCloudDto groupCloudDto = new GroupCloudDto(group, List.of(cra.getCloudResourceAccessId()));
        when(groupQueryService.getActiveGroups()).thenReturn(List.of(groupCloudDto));

        when(repository.findAllById(anySet())).thenReturn(List.of(cra));
        // Ensure repository.findAll() returns the connectors used in the loop
        when(cloudConnectorRepositoryPort.findAll()).thenReturn(List.of(cloudConnectorA));

        service.updateCostUsed();

        verify(repository).save(cra);
        assertEquals(newUsed, cra.getUsedLimit());
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
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
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
        verify(cloudConnectorClientA).cleanUpResources(GroupUniqueName.fromString("AI 2024L"), false);
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
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.of(new BigDecimal("10")))
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
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
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
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
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
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
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(10)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        CloudResourceAccess accessB = CloudResourceAccess.builder()
                .cloudResourceAccessId(idB)
                .cloudConnectorId(cloudConnectorB.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorB.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(5)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        Set<CloudResourceAccessId> ids = Set.of(idA, idB);
        when(repository.findAllById(ids)).thenReturn(List.of(accessA, accessB));

        // Act
        service.cleanUpResources(ids, group, false);

        // Assert
        verify(repository).findAllById(ids);
        verify(cloudConnectorClientA).cleanUpResources(group, false);
        verify(cloudConnectorClientB).cleanUpResources(group, false);
    }

    @Test
    @DisplayName("cleanUpResources propagates force=true to client controller")
    void cleanUpResources_forceTrue_propagated() {
        // Arrange
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());

        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(10)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        Set<CloudResourceAccessId> ids = Set.of(idA);
        when(repository.findAllById(ids)).thenReturn(List.of(accessA));

        // Act
        service.cleanUpResources(ids, group, true);

        // Assert
        verify(repository).findAllById(ids);
        verify(cloudConnectorClientA).cleanUpResources(group, true);
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
    }

    @Test
    @DisplayName("removeGroup delegates to controller when client exists")
    void removeGroup_delegates() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        service.removeGroup(group, CloudConnectorId.of("a-client"));
        verify(cloudConnectorClientA).removeGroup(group);
    }

    @Test
    @DisplayName("removeGroup throws when client does not exist")
    void removeGroup_missingClient_throws() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> service.removeGroup(group, CloudConnectorId.of("missing")));
        assertTrue(ex.getMessage().contains("CloudVendorConnectorId"));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    // ===== New tests for handleCloudConnectorCreatedEvent, countResources, getCostsByResourceTypes, getTotalCostInTime
    // =====

    @Test
    @DisplayName("handleCloudConnectorCreatedEvent registers new client via factory and it is used subsequently")
    void handleCloudConnectorCreatedEvent_registersNewClient() {
        // Arrange: new connector not present after init()
        CloudConnectorId newId = CloudConnectorId.of("c-client");
        String host = "127.0.0.1";
        int port = 9999;
        CloudConnectorClientPort cloudConnectorClientC = mock(CloudConnectorClientPort.class);
        when(cloudControllerClientFactoryPort.create(host, port)).thenReturn(cloudConnectorClientC);
        when(cloudConnectorRepositoryPort.findByClientId(newId))
                .thenReturn(Optional.of(CloudConnector.builder()
                        .cloudConnectorId(newId)
                        .host(host)
                        .port(port)
                        .resourceTypes(new ArrayList<>())
                        .build()));

        // Fire event
        service.handleCloudConnectorCreatedEvent(
                new CloudConnectorService.CloudConnectorCreatedEvent(newId, host, port));

        // Prepare a CloudResourceAccess using the new connector
        CloudResourceAccessId accessId = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccess access = CloudResourceAccess.builder()
                .cloudResourceAccessId(accessId)
                .cloudConnectorId(newId)
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        when(repository.findAllById(Set.of(accessId))).thenReturn(List.of(access));
        GroupCloudDto group = new GroupCloudDto(GroupUniqueName.fromString("AI 2024L"), List.of(accessId));
        when(cloudConnectorClientC.countCloudResources(group.groupUniqueName(), CloudResourceType.of("S3")))
                .thenReturn(5);

        // Act
        Integer result = service.countResources(group);

        // Assert
        assertEquals(5, result);
        verify(cloudControllerClientFactoryPort, times(1)).create(host, port);
        verify(cloudConnectorClientC).countCloudResources(group.groupUniqueName(), CloudResourceType.of("S3"));
    }

    @Test
    @DisplayName(
            "handleCloudConnectorCreatedEvent does not replace existing client mapping (factory may be called but existing client is used)")
    void handleCloudConnectorCreatedEvent_idempotentWhenAlreadyPresent() {
        // Arrange existing connector A (from init). Re-fire with same host/port as configured
        String host = "localhost";
        int port = 1234;

        // Stub factory to return a distinct client instance (should NOT be used by service afterwards)
        CloudConnectorClientPort replacementClient = mock(CloudConnectorClientPort.class);
        when(cloudControllerClientFactoryPort.create(host, port)).thenReturn(replacementClient);

        // Prepare an access for connector A and repository response
        GroupUniqueName groupName = GroupUniqueName.fromString("AI 2024L");
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        when(repository.findAllById(Set.of(idA))).thenReturn(List.of(accessA));
        when(cloudConnectorClientA.countCloudResources(groupName, CloudResourceType.of("S3")))
                .thenReturn(4);

        // Act: fire event twice for the same existing connector and then call a method that uses the client
        service.handleCloudConnectorCreatedEvent(new CloudConnectorService.CloudConnectorCreatedEvent(
                cloudConnectorA.getCloudConnectorId(), host, port));
        service.handleCloudConnectorCreatedEvent(new CloudConnectorService.CloudConnectorCreatedEvent(
                cloudConnectorA.getCloudConnectorId(), host, port));
        Integer count = service.countResources(new GroupCloudDto(groupName, List.of(idA)));

        // Assert: the original clientA is used, not the replacement from factory; mapping remains effective
        assertEquals(4, count);
        verify(cloudConnectorClientA).countCloudResources(groupName, CloudResourceType.of("S3"));
    }

    @Test
    @DisplayName("countResources sums counts across multiple accesses/connectors; empty returns 0")
    void countResources_sumsAndEmpty() {
        // Arrange
        GroupUniqueName groupName = GroupUniqueName.fromString("AI 2024L");
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccessId idB = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        CloudResourceAccess accessB = CloudResourceAccess.builder()
                .cloudResourceAccessId(idB)
                .cloudConnectorId(cloudConnectorB.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("EC2"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorB.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        when(repository.findAllById(Set.of(idA, idB))).thenReturn(List.of(accessA, accessB));
        when(cloudConnectorClientA.countCloudResources(groupName, CloudResourceType.of("S3")))
                .thenReturn(2);
        when(cloudConnectorClientB.countCloudResources(groupName, CloudResourceType.of("EC2")))
                .thenReturn(3);
        GroupCloudDto group = new GroupCloudDto(groupName, List.of(idA, idB));

        // Act / Assert sum
        assertEquals(5, service.countResources(group));

        // Empty case
        GroupCloudDto groupEmpty = new GroupCloudDto(groupName, List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(repository.findAllById(anySet())).thenReturn(List.of());
        assertEquals(0, service.countResources(groupEmpty));
    }

    @Test
    @DisplayName(
            "getCostsByResourceTypes merges maps and overwrites duplicate keys per putAll semantics; empty returns empty")
    void getCostsByResourceTypes_mergeAndOverwrite_andEmpty() {
        // Arrange
        GroupCloudDto group = new GroupCloudDto(GroupUniqueName.fromString("AI 2024L"), List.of());
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccessId idB = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        CloudResourceAccess accessB = CloudResourceAccess.builder()
                .cloudResourceAccessId(idB)
                .cloudConnectorId(cloudConnectorB.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorB.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        Map<CloudResourceType, BigDecimal> mapA = new HashMap<>();
        mapA.put(CloudResourceType.of("S3"), new BigDecimal("1.00"));
        mapA.put(CloudResourceType.of("EC2"), new BigDecimal("2.00"));
        Map<CloudResourceType, BigDecimal> mapB = new HashMap<>();
        mapB.put(CloudResourceType.of("S3"), new BigDecimal("3.00")); // should overwrite S3 from A

        when(repository.findAllById(Set.of(idA, idB))).thenReturn(List.of(accessA, accessB));
        when(cloudConnectorClientA.getCostsPerResourceType(group.groupUniqueName()))
                .thenReturn(mapA);
        when(cloudConnectorClientB.getCostsPerResourceType(group.groupUniqueName()))
                .thenReturn(mapB);

        GroupCloudDto groupWithIds = new GroupCloudDto(group.groupUniqueName(), List.of(idA, idB));

        // Act
        Map<CloudResourceType, BigDecimal> result = service.getCostsByResourceTypes(groupWithIds);

        // Assert merge and overwrite
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("3.00"), result.get(CloudResourceType.of("S3"))); // overwritten by B
        assertEquals(new BigDecimal("2.00"), result.get(CloudResourceType.of("EC2")));

        // Empty case
        GroupCloudDto emptyGroup =
                new GroupCloudDto(group.groupUniqueName(), List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(repository.findAllById(anySet())).thenReturn(List.of());
        assertTrue(service.getCostsByResourceTypes(emptyGroup).isEmpty());
    }

    @Test
    @DisplayName("getTotalCostInTime returns TreeMap-ordered keys and overwrites duplicate dates; empty returns empty")
    void getTotalCostInTime_orderingOverwrite_andEmpty() {
        // Arrange
        GroupUniqueName groupName = GroupUniqueName.fromString("AI 2024L");
        CloudResourceAccessId idA = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccessId idB = CloudResourceAccessId.of(UUID.randomUUID());
        CloudResourceAccess accessA = CloudResourceAccess.builder()
                .cloudResourceAccessId(idA)
                .cloudConnectorId(cloudConnectorA.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorA.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();
        CloudResourceAccess accessB = CloudResourceAccess.builder()
                .cloudResourceAccessId(idB)
                .cloudConnectorId(cloudConnectorB.getCloudConnectorId())
                .cloudResourceType(CloudResourceType.of("S3"))
                .costLimit(CostLimit.zero())
                .usedLimit(UsedLimit.empty())
                .cronExpression(cloudConnectorB.getCronExpression())
                .expiresAt(ExpiresDate.of(LocalDate.now().plusDays(1)))
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE))
                .build();

        LocalDate d1 = LocalDate.of(2024, 1, 1);
        LocalDate d2 = LocalDate.of(2024, 2, 15);
        LocalDate d0 = LocalDate.of(2023, 12, 31);
        Map<LocalDate, BigDecimal> timeA = new HashMap<>();
        timeA.put(d1, new BigDecimal("1.23"));
        timeA.put(d0, new BigDecimal("9.99"));
        Map<LocalDate, BigDecimal> timeB = new HashMap<>();
        timeB.put(d1, new BigDecimal("4.56")); // should overwrite d1
        timeB.put(d2, new BigDecimal("7.89"));

        when(repository.findAllById(Set.of(idA, idB))).thenReturn(List.of(accessA, accessB));
        when(cloudConnectorClientA.getTotalCostInTime(groupName)).thenReturn(timeA);
        when(cloudConnectorClientB.getTotalCostInTime(groupName)).thenReturn(timeB);

        GroupCloudDto groupWithIds = new GroupCloudDto(groupName, List.of(idA, idB));

        // Act
        Map<LocalDate, BigDecimal> result = service.getTotalCostInTime(groupWithIds);

        // Assert: chronological order and overwrite behavior
        assertEquals(List.of(d0, d1, d2), new ArrayList<>(result.keySet()));
        assertEquals(new BigDecimal("4.56"), result.get(d1)); // overwritten by B
        assertEquals(new BigDecimal("9.99"), result.get(d0));
        assertEquals(new BigDecimal("7.89"), result.get(d2));

        // Empty case
        GroupCloudDto emptyGroup = new GroupCloudDto(groupName, List.of(CloudResourceAccessId.of(UUID.randomUUID())));
        when(repository.findAllById(anySet())).thenReturn(List.of());
        assertTrue(service.getTotalCostInTime(emptyGroup).isEmpty());
    }

    @Test
    @DisplayName("addLecturersToGroup successfully adds lecturers")
    void addLecturersToGroup_success() {
        // Arrange
        GroupUniqueName groupName = GroupUniqueName.fromString("AI 2024L");
        Set<UserLogin> lecturers = Set.of(UserLogin.of("lecturer1"), UserLogin.of("lecturer2"));
        when(cloudConnectorClientA.addLecturerForGroup(eq(groupName), any(UserLogin.class)))
                .thenReturn(Map.entry(true, "Success"));

        // Act
        service.addLecturersToGroup(cloudConnectorA.getCloudConnectorId(), groupName, lecturers);

        // Assert
        verify(cloudConnectorClientA, times(1)).addLecturerForGroup(groupName, UserLogin.of("lecturer1"));
        verify(cloudConnectorClientA, times(1)).addLecturerForGroup(groupName, UserLogin.of("lecturer2"));
    }

    @Test
    @DisplayName("addLecturersToGroup logs failure but continues when some additions fail")
    void addLecturersToGroup_partialFailure() {
        // Arrange
        GroupUniqueName groupName = GroupUniqueName.fromString("AI 2024L");
        UserLogin successLogin = UserLogin.of("lecturer1");
        UserLogin failLogin = UserLogin.of("lecturer2");
        Set<UserLogin> lecturers = Set.of(successLogin, failLogin);

        when(cloudConnectorClientA.addLecturerForGroup(groupName, successLogin)).thenReturn(Map.entry(true, "Success"));
        when(cloudConnectorClientA.addLecturerForGroup(groupName, failLogin))
                .thenReturn(Map.entry(false, "Internal Error"));

        // Act
        service.addLecturersToGroup(cloudConnectorA.getCloudConnectorId(), groupName, lecturers);

        // Assert
        verify(cloudConnectorClientA, times(1)).addLecturerForGroup(groupName, successLogin);
        verify(cloudConnectorClientA, times(1)).addLecturerForGroup(groupName, failLogin);
    }

    @Test
    @DisplayName("addLecturersToGroup throws NoSuchElementException when connector not found")
    void addLecturersToGroup_connectorNotFound() {
        // Arrange
        CloudConnectorId missingId = CloudConnectorId.of("missing");
        when(cloudConnectorRepositoryPort.findByClientId(missingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                java.util.NoSuchElementException.class,
                () -> service.addLecturersToGroup(missingId, GroupUniqueName.fromString("AI 2024L"), Set.of()));
    }
}
