package com.unicloudapp.cloud.application.port;

import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.cloud.domain.connector.CloudConnector;

import java.util.List;
import java.util.Optional;

public interface CloudConnectorRepositoryPort {

    void save(CloudConnector CloudConnector);

    Optional<CloudConnector> findByClientId(CloudConnectorId clientId);

    List<CloudConnector> findAll();
}
