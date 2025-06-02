package com.unicloudapp.cloudmanagment.infrastructure.rest;

import com.unicloudapp.cloudmanagment.application.CloudAccessService;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/{cloudAccessClientId}")
    @ResponseStatus(HttpStatus.OK)
    List<CloudResourceType> getCloudResourceTypesForCloudAccessClient(
            @PathVariable CloudAccessClientId cloudAccessClientId
    ) {
        return cloudAccessService.getCloudResourceTypesForCloudAccessClient(cloudAccessClientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CloudAccessClientId> getCloudAccesses() {
        return cloudAccessService.getCloudAccessClients();
    }
}
