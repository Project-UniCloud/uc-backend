package com.unicloudapp.cloudmanagment.infrastructure.rest;

import com.unicloudapp.cloudmanagment.application.CloudAccessService;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
class CloudAccessRestController {

    private final CloudAccessService cloudAccessService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    UUID giveCloudAccess(@RequestBody @Valid GiveCloudAccessRequest request) {
        if (!cloudAccessService.isCloudClientExists(CloudAccessClientId.of(request.cloudAccessId()))) {
            throw new IllegalArgumentException("Given cloud access client doesn't exist");
        }
        return cloudAccessService.giveCloudAccess(UserId.of(request.userId()),
                CloudAccessClientId.of(request.cloudAccessId())
        ).getCloudAccessId().getValue();
    }
}
