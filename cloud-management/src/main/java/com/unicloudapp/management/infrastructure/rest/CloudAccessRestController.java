package com.unicloudapp.management.infrastructure.rest;

import com.unicloudapp.management.application.CloudResourceAccessService;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClient;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
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
    @GetMapping("/client/{CloudAccessClientId}/resource-types")
    @ResponseStatus(HttpStatus.OK)
    List<CloudResourceType> getCloudResourceTypesForCloudResourceAccessClient(
            @PathVariable CloudAccessClientId CloudAccessClientId
    ) {
        return CloudResourceAccessService.getCloudResourceTypesForCloudResourceAccessClient(CloudAccessClientId);
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
                        .CloudAccessClientId(client.getCloudAccessClientId().getValue())
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
        CloudResourceAccessClient details = CloudResourceAccessService.getCloudResourceAccessClientDetails(CloudAccessClientId.of(id));
        return CloudResourceAccessClientDetails.builder()
                .CloudAccessClientId(details.getCloudAccessClientId().getValue())
                .CloudResourceAccessClientName(details.getName())
                .costLimit(details.getDefaultCostLimit().getCost())
                .defaultCronExpression(details.getCronExpression().toString())
                .isActive(false)
                .build();
    }
}
