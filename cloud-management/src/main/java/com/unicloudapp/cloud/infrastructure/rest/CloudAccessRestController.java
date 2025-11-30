package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.CloudResourceAccessService;
import com.unicloudapp.cloud.domain.vendor_connector.CloudVendorConnector;
import com.unicloudapp.common.vo.cloud.CloudVendorConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
class CloudResourceAccessRestController {

    private final CloudResourceAccessService CloudResourceAccessService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client/{CloudVendorConnectorId}/resource-types")
    @ResponseStatus(HttpStatus.OK)
    List<CloudResourceType> getCloudResourceTypesForCloudResourceAccessClient(
            @PathVariable CloudVendorConnectorId cloudVendorConnectorId
    ) {
        return CloudResourceAccessService.getCloudResourceTypesForCloudResourceAccessClient(cloudVendorConnectorId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client")
    @ResponseStatus(HttpStatus.OK)
    Page<CloudResourceAccessClientRowView> getCloudResourceAccesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return CloudResourceAccessService.getCloudResourceAccessClients(PageRequest.of(page, pageSize))
                .map(client -> CloudResourceAccessClientRowView.builder()
                        .CloudVendorConnectorId(client.getCloudVendorConnectorId().id())
                        .CloudResourceAccessClientName(client.getName())
                        .costLimit(client.getDefaultCostLimit().getCost())
                        .defaultCronExpression(client.getCronExpression().toString())
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client/{id}")
    @ResponseStatus(HttpStatus.OK)
    CloudResourceAccessClientDetails getCloudResourceAccessClient(
            @PathVariable String id
    ) {
        CloudVendorConnector details = CloudResourceAccessService.getCloudResourceAccessClientDetails(CloudVendorConnectorId.of(id));
        return CloudResourceAccessClientDetails.builder()
                .CloudVendorConnectorId(details.getCloudVendorConnectorId().id())
                .CloudResourceAccessClientName(details.getName())
                .costLimit(details.getDefaultCostLimit().getCost())
                .defaultCronExpression(details.getCronExpression().toString())
                .isActive(false)
                .build();
    }
}
