package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.common.cloud.CloudAccessQueryService;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CloudAccessService implements CloudAccessQueryService {

    private final Map<String, CloudAccessClient> clients;
    private final CloudAccessRepositoryPort cloudAccessRepository;

    public CloudAccess giveCloudAccess(UserId userId,
                                       CloudAccessClientId cloudAccessClientId
    ) {
        if (!isCloudClientExists(cloudAccessClientId)) {
            throw new IllegalArgumentException("CloudAccessClientId " + cloudAccessClientId + " does not exist");
        }
        CloudAccessClient cloudAccessClient = clients.get(cloudAccessClientId.getValue());
        return cloudAccessRepository.save(
                cloudAccessClient.giveCloudAccess(userId, cloudAccessClientId)
        );
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

    @Override
    public Map<String, String> getCloudAccessNames(Set<String> cloudAccessIds) {
        return clients.entrySet()
                .stream()
                .filter(entry -> cloudAccessIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getName()));
    }
}
