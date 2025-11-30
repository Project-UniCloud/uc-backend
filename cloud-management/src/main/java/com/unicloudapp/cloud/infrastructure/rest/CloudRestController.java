package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.CloudResourceAccessService;
import com.unicloudapp.cloud.application.CloudConnectorService;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
class CloudRestController {

    private final CloudResourceAccessService cloudResourceAccessService;
    private final CloudConnectorService cloudConnectorService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/connector/{cloudConnectorId}/resource-types")
    @ResponseStatus(HttpStatus.OK)
    List<CloudResourceType> getCloudResourceTypesForCloudResourceAccessClient(
            @PathVariable CloudConnectorId cloudConnectorId
    ) {
        return cloudResourceAccessService.getCloudResourceTypesForCloudResourceAccessClient(cloudConnectorId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/connector")
    @ResponseStatus(HttpStatus.OK)
    Page<CloudConnectorRowView> getCloudConnectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return cloudResourceAccessService.getCloudResourceAccessClients(PageRequest.of(page, pageSize))
                .map(client -> CloudConnectorRowView.builder()
                        .cloudVendorConnectorId(client.getCloudConnectorId().id())
                        .cloudResourceAccessClientName(client.getName())
                        .costLimit(client.getDefaultCostLimit().getCost())
                        .defaultCronExpression(client.getCronExpression().toString())
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/connector/{cloudConnectorId}")
    @ResponseStatus(HttpStatus.OK)
    CloudConnectorDetailsDto getCloudConnectorDetails(
            @PathVariable String cloudConnectorId
    ) {
        CloudConnector details = cloudResourceAccessService.getCloudResourceAccessClientDetails(CloudConnectorId.of(cloudConnectorId));
        return CloudConnectorDetailsDto.builder()
                .cloudConnectorId(details.getCloudConnectorId().id())
                .name(details.getName())
                .costLimit(details.getDefaultCostLimit().getCost())
                .defaultCronExpression(details.getCronExpression().toString())
                .isActive(false)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/connector")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Void> postCloudConnector(
            @RequestBody CloudConnectorSaveRequestDto request
    ) {
        var connector = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of(request.cloudConnectorId()))
                .name(request.name())
                .host(request.host())
                .port(request.port())
                .controller()
                .build();
        cloudConnectorService.createConnector();
    }
}
