package com.unicloudapp.management.application.port;

import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClient;

import java.util.List;
import java.util.Optional;

public interface CloudResourceAccessClientRepositoryPort {

    void save(CloudResourceAccessClient CloudResourceAccessClient);

    Optional<CloudResourceAccessClient> findByClientId(CloudAccessClientId clientId);

    List<CloudResourceAccessClient> findAll();
}
