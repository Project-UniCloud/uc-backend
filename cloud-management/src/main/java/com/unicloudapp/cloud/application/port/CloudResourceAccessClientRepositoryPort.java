package com.unicloudapp.cloud.application.port;

import com.unicloudapp.common.vo.cloud.CloudVendorConnectorId;
import com.unicloudapp.cloud.domain.vendor_connector.CloudVendorConnector;

import java.util.List;
import java.util.Optional;

public interface CloudResourceAccessClientRepositoryPort {

    void save(CloudVendorConnector CloudVendorConnector);

    Optional<CloudVendorConnector> findByClientId(CloudVendorConnectorId clientId);

    List<CloudVendorConnector> findAll();
}
