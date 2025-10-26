package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.cloudmanagment.domain.CloudResourceAccess;
import com.unicloudapp.cloudmanagment.domain.CloudResourcesAccessStatus;
import com.unicloudapp.cloudmanagment.domain.ExpiresDate;
import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceTypeRowView;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.cloud.CostLimit;
import com.unicloudapp.common.domain.cloud.UsedLimit;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CloudAccessService
        implements CloudResourceAccessQueryService, CloudResourceAccessCommandService {

    private final Map<CloudResourceAccessId, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;
    private final Map<String, CloudAccessClient> clients;
    private final CloudResourceAccessRepositoryPort cloudAccessRepository;
    private final GroupQueryService groupQueryService;

    @PostConstruct
    protected void init() {
        List<GroupCloudDto> groupCloudDtoList = groupQueryService.getActiveGroups();
        CloudResourcesAccessStatus activeStatus = CloudResourcesAccessStatus.of(
                CloudResourcesAccessStatus.Status.ACTIVE
        );
        Map<CloudResourceAccessId, CloudResourceAccess> activeCloudResourcesAccesses =
                cloudAccessRepository.findAllByStatus(activeStatus);
        groupCloudDtoList.forEach(groupCloudDto ->
                groupCloudDto.cloudResourceAccesses()
                        .stream()
                        .filter(activeCloudResourcesAccesses::containsKey)
                        .forEach(cloudResourceAccessId ->
                            scheduleTask(activeCloudResourcesAccesses.get(cloudResourceAccessId), groupCloudDto.groupUniqueName())
                        )
        );
    }

    private void scheduleTask(CloudResourceAccess cloudResourceAccess, GroupUniqueName groupUniqueName) {
        CronTrigger cronTrigger = new CronTrigger(cloudResourceAccess.getCronExpression().toString());
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> cleanUpResources(cloudResourceAccess, groupUniqueName),
                cronTrigger
        );
        scheduledTasks.put(cloudResourceAccess.getCloudResourceAccessId(), future);
    }

    public boolean isRunning(CloudAccessClientId cloudAccessClientId) {
        if (!isCloudClientExists(cloudAccessClientId)) {
            throw new IllegalArgumentException("CloudAccessClientId " + cloudAccessClientId + " does not exist");
        }
        CloudAccessClient cloudAccessClient = clients.get(cloudAccessClientId.getValue());
        return cloudAccessClient.getController().isRunning();
    }

    public boolean isCloudClientExists(CloudAccessClientId cloudAccessClientId) {
        return clients.containsKey(cloudAccessClientId.getValue());
    }

    public List<CloudResourceType> getCloudResourceTypesForCloudAccessClient(
            CloudAccessClientId cloudAccessClientId
    ) {
        if (!isCloudClientExists(cloudAccessClientId)) {
            throw new IllegalArgumentException("CloudAccessClientId " + cloudAccessClientId + " does not exist");
        }
        CloudAccessClient cloudAccessClient = clients.get(cloudAccessClientId.getValue());
        return cloudAccessClient.getResourceTypes();
    }

    @Override
    public Set<CloudResourceType> getCloudResourceTypes(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        return cloudAccessRepository.getCloudResourceAccesses(cloudResourceAccessIds)
                .stream()
                .map(CloudResourceAccess::getCloudResourceType)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isCloudGroupExists(GroupUniqueName groupUniqueName,
                                      CloudAccessClientId cloudAccessClientId
    ) {

        if (!isCloudClientExists(cloudAccessClientId)) {
            throw new IllegalArgumentException("CloudAccessClientId " + cloudAccessClientId + " does not exist");
        }
        CloudAccessClient cloudAccessClient = clients.get(cloudAccessClientId.getValue());
        return cloudAccessClient.isCloudGroupExists(groupUniqueName);
    }

    @Override
    public List<CloudResourceTypeRowView> getCloudResourceTypesDetails(Set<CloudResourceAccessId> cloudResourceAccessIds) {
        List<CloudResourceAccess> cloudResourceAccesses = cloudAccessRepository.findAllById(cloudResourceAccessIds);
        return cloudResourceAccesses.stream()
                .map(cloudResourceAccess -> CloudResourceTypeRowView.builder()
                        .name(cloudResourceAccess.getCloudResourceType().getName())
                        .costLimit(cloudResourceAccess.getCostLimit().getCost())
                        .clientId(cloudResourceAccess.getCloudAccessClientId().getValue())
                        .status(cloudResourceAccess.getStatus().getStatus().name())
                        .cronCleanupSchedule(cloudResourceAccess.getCronExpression().toString())
                        .lastUsedAt(LocalDateTime.now())
                        .expiresAt(cloudResourceAccess.getExpiresAt().getValue())
                        .limitUsed(cloudResourceAccess.getUsedLimit()
                                .getValue())
                        .build())
                .toList();
    }

    @Override
    public Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientIdAndResourceType(
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType resourceType
    ) {
        return cloudAccessRepository.findAllByCloudClientIdAndResourceType(cloudAccessClientId, resourceType)
                .stream()
                .map(CloudResourceAccess::getCloudResourceAccessId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CloudResourceAccessId> getCloudResourceAccessesByCloudClientId(CloudAccessClientId cloudAccessClientId) {
        return cloudAccessRepository.findAllByCloudClientId(cloudAccessClientId)
                .stream()
                .map(CloudResourceAccess::getCloudResourceAccessId)
                .collect(Collectors.toSet());
    }

    @Override
    public CloudResourceAccessId giveGroupCloudResourceAccess(CloudAccessClientId cloudAccessClientId,
                                                              CloudResourceType cloudResourceType,
                                                              GroupUniqueName groupUniqueName,
                                                              CostLimit costLimit
    ) {
        if (!isCloudClientExists(cloudAccessClientId)) {
            throw new IllegalArgumentException("CloudAccessClientId " + cloudAccessClientId + " does not exist");
        }
        CloudAccessClient cloudAccessClient = clients.get(cloudAccessClientId.getValue());
        if (!cloudAccessClient.containsResourceType(cloudResourceType)) {
            throw new IllegalArgumentException("CloudResourceType " + cloudResourceType + " is not supported by client " + cloudAccessClientId);
        }
        CloudResourceAccess cloudResourceAccess = cloudAccessClient.getCloudResourceAccessFactory()
                .create(
                        CloudResourceAccessId.of(UUID.randomUUID()),
                        cloudAccessClient.getCloudAccessClientId(),
                        cloudResourceType,
                        costLimit,
                        cloudAccessClient.getCronExpression(),
                        ExpiresDate.of(LocalDate.now().plusDays(30)) //TODO inject this value
                );
        cloudAccessRepository.save(cloudResourceAccess);
        scheduleTask(cloudResourceAccess, groupUniqueName);
        return cloudResourceAccess.getCloudResourceAccessId();
    }

    @Override
    public void createGroup(GroupUniqueName groupUniqueName,
                            CloudAccessClientId cloudAccessClientId,
                            List<UserLogin> lecturerLogins,
                            CloudResourceType resourceType
    ) {
        if (!isCloudClientExists(cloudAccessClientId)) {
            throw new IllegalArgumentException("CloudAccessClientId " + cloudAccessClientId + " does not exist");
        }
        clients.get(cloudAccessClientId.getValue()).createGroup(groupUniqueName, lecturerLogins, resourceType);
    }

    public Page<CloudAccessClient> getCloudAccessClients(Pageable pageable) {
        return new PageImpl<>(
                clients.values().stream()
                        .sorted(Comparator.comparing(c -> c.getCloudAccessClientId()
                                .getValue()))
                        .toList(),
                pageable,
                clients.size()
        );
    }

    public CloudAccessClient getCloudAccessClientDetails(CloudAccessClientId clientId) {
        return clients.get(clientId.getValue());
    }

    @Override
    public String createUsers(CloudAccessClientId cloudAccessClientId, List<UserLogin> users, GroupUniqueName groupUniqueName) {
        return clients.get(cloudAccessClientId.getValue()).createUsers(users, groupUniqueName);
    }

    @Override
    @Transactional
    public void activateCloudResource(CloudResourceAccessId cloudResourceAccessId) {
        Optional<CloudResourceAccess> resourceAccess = cloudAccessRepository.findById(cloudResourceAccessId);
        resourceAccess.ifPresent(cloudResourceAccess -> {
            cloudResourceAccess.active();
            cloudAccessRepository.save(resourceAccess.get());
        });
    }

    @Scheduled(cron = "0 0 * * * *")
    protected void updateCostUsed() {
        clients.values()
                .forEach(cloudAccessClient -> {
                    Map<GroupUniqueName, UsedLimit> groupUniqueNameUsedLimitMap = cloudAccessClient.updateUsedCost();
                    groupUniqueNameUsedLimitMap.forEach((groupUniqueName, usedLimit) -> {
                        Set<CloudResourceAccessId> cloudResourceAccessIds = getCloudResourceAccessesByCloudClientIdAndResourceType(
                                cloudAccessClient.getCloudAccessClientId(),
                                cloudAccessClient.getResourceTypes().getFirst()
                        );
                        List<CloudResourceAccess> allById = cloudAccessRepository.findAllById(cloudResourceAccessIds);
                        allById.forEach(cloudAccess -> {
                            cloudAccess.updateUsedLimit(usedLimit);
                            cloudAccessRepository.save(cloudAccess);
                        });
                    });
                });
    }

    private void cleanUpResources(CloudResourceAccess cloudResourceAccess, GroupUniqueName groupUniqueName) {
        clients.get(cloudResourceAccess.getCloudAccessClientId().getValue())
                .cleanUpResources(groupUniqueName, true);
    }
}
