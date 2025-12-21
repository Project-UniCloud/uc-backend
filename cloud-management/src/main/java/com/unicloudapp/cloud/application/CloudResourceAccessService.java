package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorClientFactoryPort;
import com.unicloudapp.cloud.application.port.CloudConnectorClientPort;
import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.application.port.CloudResourceAccessRepositoryPort;
import com.unicloudapp.cloud.domain.access.CloudResourceAccess;
import com.unicloudapp.cloud.domain.access.CloudResourceAccessFactory;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.cloud.domain.vo.ExpiresDate;
import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.cloud.event.CloudUserCreatedEvent;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CloudResourceAccessService
        implements CloudResourceAccessQueryService, CloudResourceAccessCommandService {

    private final Map<CloudResourceAccessId, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<CloudConnectorId, CloudConnectorClientPort> cloudConnectorClients = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;
    private final CloudConnectorRepositoryPort cloudConnectorRepositoryPort;
    private final CloudResourceAccessRepositoryPort cloudResourceAccessRepository;
    private final GroupQueryService groupQueryService;
    private final CloudResourceAccessFactory cloudResourceAccessFactory;
    private final CloudConnectorClientFactoryPort cloudConnectorClientFactoryPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    //extract to another bean to make it transactional
    @PostConstruct
    protected void init() {
        List<GroupCloudDto> groupCloudDtoList = groupQueryService.getActiveGroups();
        CloudResourcesAccessStatus activeStatus = CloudResourcesAccessStatus.of(
                CloudResourcesAccessStatus.Status.ACTIVE
        );
        Map<CloudResourceAccessId, CloudResourceAccess> activeCloudResourcesAccesses =
                cloudResourceAccessRepository.findAllByStatus(activeStatus);
        groupCloudDtoList.forEach(groupCloudDto ->
                groupCloudDto.cloudResourceAccesses()
                        .stream()
                        .filter(activeCloudResourcesAccesses::containsKey)
                        .forEach(cloudResourceAccessId ->
                            scheduleTask(activeCloudResourcesAccesses.get(cloudResourceAccessId), groupCloudDto.groupUniqueName())
                        )
        );
        cloudConnectorRepositoryPort.findAll()
                .forEach(cloudConnector -> {
                    CloudConnectorClientPort connectorClient = cloudConnectorClientFactoryPort.create(
                            cloudConnector.getHost(), cloudConnector.getPort()
                    );
                    List<CloudResourceType> supportedResourceTypes = connectorClient.getSupportedResourceTypes();
                    cloudConnector.syncResourceTypes(supportedResourceTypes);
                    cloudConnectorRepositoryPort.save(cloudConnector);
                    cloudConnectorClients.put(
                            cloudConnector.getCloudConnectorId(),
                            connectorClient
                    );
                });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void handleCloudConnectorCreatedEvent(CloudConnectorService.CloudConnectorCreatedEvent event) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(event.cloudConnectorId())
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + event.cloudConnectorId() + " does not exist"));
        CloudConnectorClientPort cloudConnectorClient = cloudConnectorClientFactoryPort.create(event.host(), event.port());
        cloudConnectorClients.putIfAbsent(event.cloudConnectorId(), cloudConnectorClient);
        List<CloudResourceType> supportedResourceTypes = cloudConnectorClient.getSupportedResourceTypes();
        cloudConnector.syncResourceTypes(supportedResourceTypes);
        cloudConnectorRepositoryPort.save(cloudConnector);
    }

    public Integer countResources(GroupCloudDto groupCloudDto) {
        Set<CloudResourceAccessId> cloudResourceAccessIds = new HashSet<>(groupCloudDto.cloudResourceAccesses());
        int result = 0;
        for (CloudResourceAccess cloudResourceAccess : cloudResourceAccessRepository.findAllById(cloudResourceAccessIds)) {
            result += cloudConnectorClients.get(cloudResourceAccess.getCloudConnectorId())
                    .countCloudResources(groupCloudDto.groupUniqueName(), cloudResourceAccess.getCloudResourceType());
        }
        return result;
    }

    public Map<CloudResourceType, BigDecimal> getCostsByResourceTypes(GroupCloudDto groupCloudDto) {
        Set<CloudResourceAccessId> cloudResourceAccessIds = new HashSet<>(groupCloudDto.cloudResourceAccesses());
        Map<CloudResourceType, BigDecimal> result = new HashMap<>();
        for (CloudResourceAccess cloudResourceAccess : cloudResourceAccessRepository.findAllById(cloudResourceAccessIds)) {
            Map<CloudResourceType, BigDecimal> costsPerResourceType = cloudConnectorClients.get(cloudResourceAccess.getCloudConnectorId())
                    .getCostsPerResourceType(groupCloudDto.groupUniqueName());
            result.putAll(costsPerResourceType);
        }
        return result;
    }

    public Map<LocalDate, BigDecimal> getTotalCostInTime(GroupCloudDto groupCloudDto) {
        Set<CloudResourceAccessId> cloudResourceAccessIds = new HashSet<>(groupCloudDto.cloudResourceAccesses());
        Map<LocalDate, BigDecimal> result = new TreeMap<>();
        for (CloudResourceAccess cloudResourceAccess : cloudResourceAccessRepository.findAllById(cloudResourceAccessIds)) {
            Map<LocalDate, BigDecimal> costsPerResourceType = cloudConnectorClients.get(cloudResourceAccess.getCloudConnectorId())
                    .getTotalCostInTime(groupCloudDto.groupUniqueName());
            result.putAll(costsPerResourceType);
        }
        return result;
    }

    public boolean isRunning(CloudConnectorId cloudConnectorId) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        return cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).isRunning();
    }

    public boolean isCloudClientExists(CloudConnectorId cloudConnectorId) {
        return cloudConnectorRepositoryPort.findByClientId(cloudConnectorId).isPresent();
    }

    public Page<CloudResourceType> getCloudResourceTypesForCloudResourceAccessClient(
            Pageable pageable,
            CloudConnectorId cloudConnectorId
    ) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        return new PageImpl<>(
                cloudConnector.getResourceTypes(),
                pageable,
                cloudConnector.getResourceTypes().size()
        );
    }

    // Backward-compatible overload used by older tests calling a single-argument version.
    public List<CloudResourceType> getCloudResourceTypesForCloudResourceAccessClient(
            CloudConnectorId cloudConnectorId
    ) {
        Page<CloudResourceType> page = getCloudResourceTypesForCloudResourceAccessClient(
                PageRequest.of(0, Integer.MAX_VALUE, Sort.unsorted()),
                cloudConnectorId
        );
        return page.getContent();
    }

    @Override
    public Set<CloudResourceType> getCloudResourceTypes(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        return cloudResourceAccessRepository.getCloudResourceAccesses(cloudResourceAccessIds)
                .stream()
                .map(CloudResourceAccess::getCloudResourceType)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isCloudGroupExists(GroupUniqueName groupUniqueName,
                                      CloudConnectorId cloudConnectorId
    ) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        return cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).isCloudGroupExists(groupUniqueName);
    }

    @Override
    public List<CloudResourceRowView> getCloudResourceDetails(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        List<CloudResourceAccess> CloudResourceAccesses = cloudResourceAccessRepository.findAllById(cloudResourceAccessIds);
        return CloudResourceAccesses.stream()
                .map(cloudResourceAccess -> CloudResourceRowView.builder()
                        .id(cloudResourceAccess.getCloudResourceAccessId().getValue())
                        .name(cloudResourceAccess.getCloudResourceType().getName())
                        .costLimit(cloudResourceAccess.getCostLimit().getCost())
                        .clientId(cloudResourceAccess.getCloudConnectorId().id())
                        .status(cloudResourceAccess.getStatus().getStatus().name())
                        .cronCleanupSchedule(cloudResourceAccess.getCronExpression().toString())
                        .lastUsedAt(LocalDateTime.now())
                        .expiresAt(cloudResourceAccess.getExpiresAt().getValue())
                        .limitUsed(cloudResourceAccess.getUsedLimit()
                                .getValue())
                        .notificationLevel1(cloudResourceAccess.getNotificationLevel1().level())
                        .notificationLevel2(cloudResourceAccess.getNotificationLevel2().level())
                        .notificationLevel3(cloudResourceAccess.getNotificationLevel3().level())
                        .build())
                .toList();
    }

    @Override
    public Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientIdAndResourceType(
            CloudConnectorId cloudConnectorId,
            CloudResourceType resourceType
    ) {
        return cloudResourceAccessRepository.findAllByCloudClientIdAndResourceType(cloudConnectorId, resourceType)
                .stream()
                .map(CloudResourceAccess::getCloudResourceAccessId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientId(CloudConnectorId cloudConnectorId) {
        return cloudResourceAccessRepository.findAllByCloudClientId(cloudConnectorId)
                .stream()
                .map(CloudResourceAccess::getCloudResourceAccessId)
                .collect(Collectors.toSet());
    }

    @Override
    public CloudResourceAccessId giveGroupCloudResourceAccess(CloudConnectorId cloudConnectorId,
                                                              CloudResourceType cloudResourceType,
                                                              GroupUniqueName groupUniqueName,
                                                              CostLimit costLimit
    ) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        if (!cloudConnector.containsResourceType(cloudResourceType)) {
            throw new IllegalArgumentException("CloudResourceType " + cloudResourceType + " is not supported by client " + cloudConnectorId);
        }
        CloudResourceAccess cloudResourceAccess = cloudResourceAccessFactory
                .create(
                        CloudResourceAccessId.of(UUID.randomUUID()),
                        cloudConnector.getCloudConnectorId(),
                        cloudResourceType,
                        costLimit,
                        cloudConnector.getCronExpression(),
                        ExpiresDate.of(LocalDate.now().plusDays(30)) //TODO inject this value
                );
        cloudResourceAccessRepository.save(cloudResourceAccess);
        scheduleTask(cloudResourceAccess, groupUniqueName);
        return cloudResourceAccess.getCloudResourceAccessId();
    }

    @Override
    public void createGroup(GroupUniqueName groupUniqueName,
                            CloudConnectorId cloudConnectorId,
                            List<Map.Entry<UserLogin, Email>> lecturers,
                            CloudResourceType resourceType
    ) {
        lecturers.forEach(lecturer -> {
            CloudUserCreatedEvent event = CloudUserCreatedEvent.builder()
                    .userLogin(lecturer.getKey())
                    .build();
            applicationEventPublisher.publishEvent(event);
        });
        val lecturerLogins = lecturers.stream().map(Map.Entry::getKey).toList();
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).createGroup(groupUniqueName, lecturerLogins, resourceType);
    }

    public Page<CloudConnector> getCloudResourceAccessClients(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("id")
        );
        return cloudConnectorRepositoryPort.findAll(pageRequest);
    }

    public CloudConnector getCloudResourceAccessClientDetails(CloudConnectorId clientId) {
        return cloudConnectorRepositoryPort.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + clientId + " does not exist"));
    }

    @Override
    public String createUsers(CloudConnectorId cloudConnectorId, List<Map.Entry<UserLogin, Email>> users, GroupUniqueName groupUniqueName) {
        final var logins = users.stream().map(Map.Entry::getKey).toList();
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        String createdUserLogin = cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).createUsers(logins, groupUniqueName);
        users.forEach(user -> {
            CloudUserCreatedEvent event = CloudUserCreatedEvent.builder()
                    .userLogin(user.getKey())
                    .build();
            applicationEventPublisher.publishEvent(event);
        });
        return createdUserLogin;
    }

    @Override
    @Transactional
    public void activateCloudResource(CloudResourceAccessId cloudResourceAccessId) {
        Optional<CloudResourceAccess> resourceAccess = cloudResourceAccessRepository.findById(cloudResourceAccessId);
        resourceAccess.ifPresent(cloudResourceAccess -> {
            cloudResourceAccess.active();
            cloudResourceAccessRepository.save(cloudResourceAccess);
        });
    }

    @Transactional
    @Override
    public void updateGroupCloudResourceAccess(CloudResourceAccessDetailsDto request, GroupUniqueName groupUniqueName) {
        Optional<CloudResourceAccess> resourceAccess = cloudResourceAccessRepository.findById(CloudResourceAccessId.of(request.id()));
        resourceAccess.ifPresent(cloudResourceAccess -> {
            cloudResourceAccess.update(request);
            updateScheduledTask(
                    cloudResourceAccess,
                    groupUniqueName
            );
            cloudResourceAccessRepository.save(cloudResourceAccess);
        });
    }

    @Transactional
    @Override
    public void deactivateCloudResourceAccess(CloudResourceAccessId cloudResourceAccessId) {
        Optional<CloudResourceAccess> resourceAccess = cloudResourceAccessRepository.findById(cloudResourceAccessId);
        resourceAccess.ifPresent(cloudResourceAccess -> {
            cloudResourceAccess.deactivate();
            cancelScheduledTask(cloudResourceAccessId);
            cloudResourceAccessRepository.save(cloudResourceAccess);
        });
    }

    @Override
    public void cleanUpResources(Set<CloudResourceAccessId> CloudVendorConnectorIds, GroupUniqueName groupUniqueName, boolean force) {
        cloudResourceAccessRepository.findAllById(CloudVendorConnectorIds).forEach(cloudResourceAccess ->
                cleanUpResources(cloudResourceAccess, groupUniqueName, force)
        );
    }

    @Override
    public void removeGroup(GroupUniqueName groupUniqueName, CloudConnectorId cloudConnectorId) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).removeGroup(groupUniqueName);
    }

    @Override
    public void assignCloudResourceAccess(CloudConnectorId cloudConnectorId, CloudResourceType cloudResourceType, UserLogin lecturerLogin) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).assignCloudResourceAccess(cloudResourceType, null, lecturerLogin);
    }

    @Override
    public void assignCloudResourceAccess(CloudConnectorId cloudConnectorId, GroupUniqueName groupUniqueName, CloudResourceType cloudResourceType) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudConnectorId)
                .orElseThrow(() -> new IllegalArgumentException("CloudVendorConnectorId " + cloudConnectorId + " does not exist"));
        cloudConnectorClients.get(cloudConnector.getCloudConnectorId()).assignCloudResourceAccess(cloudResourceType, groupUniqueName, null);
    }

    @Override
    @Transactional
    public void updateCloudResourceAccessClientDetails(CloudConnectorId of, CostLimit of1, CronExpression parse, String cloudConnectorName) {
        Optional<CloudConnector> cloudConnector = cloudConnectorRepositoryPort.findByClientId(of);
        cloudConnector.ifPresent(t -> {
            t.updateName(cloudConnectorName);
            t.updateCostLimit(of1);
            t.updateCron(parse);
            cloudConnectorRepositoryPort.save(t);
        });
    }

    @Scheduled(cron = "${adapters.costSyncCron}")
    @Transactional
    protected void updateCostUsed() {
        cloudConnectorRepositoryPort.findAll()
                .forEach(cloudResourceAccessClient -> {
                    CloudConnectorClientPort cloudConnectorClient = cloudConnectorClients.get(cloudResourceAccessClient.getCloudConnectorId());
                    var now = LocalDate.now();
                    groupQueryService.getActiveGroups().forEach(group -> {
                        List<CloudResourceAccess> cloudResourceAccesses = cloudResourceAccessRepository.findAllById(new HashSet<>(group.cloudResourceAccesses()));
                        cloudResourceAccesses.forEach(cloudResourceAccess -> {
                            UsedLimit cost = cloudConnectorClient.updateUsedCost(now.minusYears(1), now, group.groupUniqueName());
                            cloudResourceAccess.updateUsedLimit(cost);
                            cloudResourceAccessRepository.save(cloudResourceAccess);
                        });
                    });
                });
    }

    private void cleanUpResources(CloudResourceAccess cloudResourceAccess, GroupUniqueName groupUniqueName, boolean force) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort.findByClientId(cloudResourceAccess.getCloudConnectorId())
                .orElseThrow();
        cloudConnectorClients.get(cloudConnector.getCloudConnectorId())
                .cleanUpResources(groupUniqueName, force);
    }

    private void scheduleTask(CloudResourceAccess CloudResourceAccess, GroupUniqueName groupUniqueName) {
        CronTrigger cronTrigger = new CronTrigger(CloudResourceAccess.getCronExpression().toString());
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> cleanUpResources(CloudResourceAccess, groupUniqueName, false),
                cronTrigger
        );
        scheduledTasks.put(CloudResourceAccess.getCloudResourceAccessId(), future);
    }

    private void updateScheduledTask(
            CloudResourceAccess CloudResourceAccess,
            GroupUniqueName groupUniqueName
    ) {
        cancelScheduledTask(CloudResourceAccess.getCloudResourceAccessId());
        scheduleTask(CloudResourceAccess, groupUniqueName);
    }

    private void cancelScheduledTask(CloudResourceAccessId cloudResourceAccessId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(cloudResourceAccessId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledTasks.remove(cloudResourceAccessId);
    }
}
