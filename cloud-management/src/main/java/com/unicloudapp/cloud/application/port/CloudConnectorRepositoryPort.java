package com.unicloudapp.cloud.application.port;

import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CloudConnectorRepositoryPort {

    void save(CloudConnector CloudConnector);

    Optional<CloudConnector> findByClientId(CloudConnectorId clientId);

    List<CloudConnector> findAll();

    Page<@NotNull CloudConnector> findAll(Pageable pageable);
}
