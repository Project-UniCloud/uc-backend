package com.unicloudapp.cloudmanagment.infrastructure.rest;

import com.unicloudapp.cloudmanagment.application.CloudAccessService;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
class CloudAccessRestController {

    private final CloudAccessService cloudAccessService;

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
