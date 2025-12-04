package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.CloudConnectorService;
import com.unicloudapp.cloud.application.CloudResourceAccessService;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.CronExpression;
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
                .map(cloudConnector -> CloudConnectorRowView.builder()
                        .cloudConnectorId(cloudConnector.getCloudConnectorId().id())
                        .cloudConnectorName(cloudConnector.getName())
                        .costLimit(cloudConnector.getDefaultCostLimit().getCost())
                        .defaultCronExpression(cloudConnector.getCronExpression().toString())
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
                .cloudConnectorName(details.getName())
                .costLimit(details.getDefaultCostLimit().getCost())
                .defaultCronExpression(details.getCronExpression().toString())
                .isActive(false)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/connector")
    @ResponseStatus(HttpStatus.CREATED)
    void postCloudConnector(
            @RequestBody CloudConnectorSaveRequestDto request
    ) {
        cloudConnectorService.createConnector(
                CloudConnectorId.of(request.cloudConnectorId()),
                request.host(),
                request.port(),
                CostLimit.of(request.defaultCostLimit()),
                CronExpression.parse(request.cronExpression()),
                request.name()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/connector/resource-type")
    @ResponseStatus(HttpStatus.OK)
    void addCloudConnectorResourceType(
            @RequestBody CloudConnectorResourceTypeRequestDto request
    ) {
        cloudConnectorService.addResourceType(
                CloudConnectorId.of(request.cloudConnectorId()),
                CloudResourceType.of(request.resourceType())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/connector/resource-type")
    @ResponseStatus(HttpStatus.OK)
    void deleteCloudConnectorResourceType(
            @RequestBody CloudConnectorResourceTypeRequestDto request
    ) {
        cloudConnectorService.deleteResourceType(
                CloudConnectorId.of(request.cloudConnectorId()),
                CloudResourceType.of(request.resourceType())
        );
    }
}
