package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.cloudmanagment.domain.CloudResourceAccess;
import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.cloud.CloudResourceTypeRowView;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CloudAccessService
        implements CloudResourceAccessQueryService, CloudResourceAccessCommandService {

    private final Map<String, CloudAccessClient> clients;
    private final CloudResourceAccessRepositoryPort cloudAccessRepository;

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
                        .status("ACTIVE") //TODO
                        .cronCleanupSchedule("0 2 */2 * *")//TODO
                        .lastUsedAt(LocalDateTime.now())//TODO
                        .expiresAt(LocalDate.now().plusDays(30))//TODO
                        .limitUsed(BigDecimal.ZERO)//TODO
                        .build())
                .toList();
    }

    @Override
    public CloudResourceAccessId giveGroupCloudResourceAccess(CloudAccessClientId cloudAccessClientId,
                                                              CloudResourceType cloudResourceType,
                                                              GroupUniqueName groupUniqueName
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
                        cloudAccessClient.getDefaultCostLimit()
                );
        cloudAccessRepository.save(cloudResourceAccess);
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

    public List<CloudAccessClientId> getCloudAccessClients() {
        return clients.keySet()
                .stream()
                .map(CloudAccessClientId::of)
                .toList();
    }
}
