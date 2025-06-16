package com.unicloudapp.cloudmanagment.infrastructure.rest;

import com.unicloudapp.cloudmanagment.application.CloudAccessService;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
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
class CloudAccessRestController {

    private final CloudAccessService cloudAccessService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client/{cloudAccessClientId}/resource-types")
    @ResponseStatus(HttpStatus.OK)
    List<CloudResourceType> getCloudResourceTypesForCloudAccessClient(
            @PathVariable CloudAccessClientId cloudAccessClientId
    ) {
        return cloudAccessService.getCloudResourceTypesForCloudAccessClient(cloudAccessClientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client")
    @ResponseStatus(HttpStatus.OK)
    Page<CloudAccessClientRowView> getCloudAccesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return cloudAccessService.getCloudAccessClients(PageRequest.of(page, pageSize))
                .map(client -> CloudAccessClientRowView.builder()
                        .cloudAccessClientId(client.getCloudAccessClientId().getValue())
                        .cloudAccessClientName(client.getName())
                        .costLimit(client.getDefaultCostLimit().getCost())
                        .defaultCronExpression(client.getCronExpression().toString())
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client/{id}")
    @ResponseStatus(HttpStatus.OK)
    CloudAccessClientDetails getCloudAccessClient(
            @PathVariable String id
    ) {
        CloudAccessClient details = cloudAccessService.getCloudAccessClientDetails(CloudAccessClientId.of(id));
        return CloudAccessClientDetails.builder()
                .cloudAccessClientId(details.getCloudAccessClientId().getValue())
                .cloudAccessClientName(details.getName())
                .costLimit(details.getDefaultCostLimit().getCost())
                .defaultCronExpression(details.getCronExpression().toString())
                .isActive(false)
                .build();
    }
}
