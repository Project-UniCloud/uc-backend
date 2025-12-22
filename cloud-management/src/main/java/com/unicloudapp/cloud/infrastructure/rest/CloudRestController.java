package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.CloudConnectorService;
import com.unicloudapp.cloud.application.CloudResourceAccessService;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
class CloudRestController {

    private final CloudResourceAccessService cloudResourceAccessService;
    private final CloudConnectorService cloudConnectorService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/connector/{cloudConnectorId}/resource-types")
    @ResponseStatus(HttpStatus.OK)
    Page<@NotNull CloudResourceType> getCloudResourceTypesForCloudResourceAccessClient(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable CloudConnectorId cloudConnectorId) {
        return cloudResourceAccessService.getCloudResourceTypesForCloudResourceAccessClient(
                PageRequest.of(page, pageSize), cloudConnectorId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/connector")
    @ResponseStatus(HttpStatus.OK)
    Page<@NotNull CloudConnectorRowView> getCloudConnectors(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return cloudResourceAccessService
                .getCloudResourceAccessClients(PageRequest.of(page, pageSize))
                .map(cloudConnector -> CloudConnectorRowView.builder()
                        .cloudConnectorId(cloudConnector.getCloudConnectorId().id())
                        .cloudConnectorName(cloudConnector.getName())
                        .costLimit(cloudConnector.getDefaultCostLimit().getCost())
                        .defaultCronExpression(
                                cloudConnector.getCronExpression().toString())
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/connector/{cloudConnectorId}")
    @ResponseStatus(HttpStatus.OK)
    CloudConnectorDetailsDto getCloudConnectorDetails(@PathVariable String cloudConnectorId) {
        CloudConnector details =
                cloudResourceAccessService.getCloudResourceAccessClientDetails(CloudConnectorId.of(cloudConnectorId));
        return CloudConnectorDetailsDto.builder()
                .cloudConnectorId(details.getCloudConnectorId().id())
                .cloudConnectorName(details.getName())
                .costLimit(details.getDefaultCostLimit().getCost())
                .defaultCronExpression(details.getCronExpression().toString())
                .isActive(false)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/connector/{cloudConnectorId}")
    @ResponseStatus(HttpStatus.OK)
    void putCloudConnectorDetails(
            @RequestBody CloudConnectorUpdateRequestDto request, @PathVariable String cloudConnectorId) {
        cloudResourceAccessService.updateCloudResourceAccessClientDetails(
                CloudConnectorId.of(cloudConnectorId),
                CostLimit.of(request.costLimit),
                CronExpression.parse(request.defaultCronExpression),
                request.cloudConnectorName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/connector")
    @ResponseStatus(HttpStatus.CREATED)
    void postCloudConnector(@RequestBody CloudConnectorSaveRequestDto request) {
        cloudConnectorService.createConnector(
                CloudConnectorId.of(request.cloudConnectorId()),
                request.host(),
                request.port(),
                CostLimit.of(request.defaultCostLimit()),
                CronExpression.parse(request.cronExpression()),
                request.name());
    }

    private record CloudConnectorUpdateRequestDto(
            BigDecimal costLimit, String defaultCronExpression, String cloudConnectorName) {}
}
