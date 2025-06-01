package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class CloudAccessClientTest {

    private final CloudAccessClientController controller = Mockito.mock(CloudAccessClientController.class);
    private final CloudAccessClient cloudAccessClient = CloudAccessClient.builder()
            .cloudAccessClientId(CloudAccessClientId.of("aws"))
            .controller(controller)
            .build();

    /*@Test
    void giveCloudAccess() {
        cloudAccessClient.giveCloudAccess(UserId.of(UUID.randomUUID()), CloudAccessClientId.of("aws"));
        verify(controller).giveCloudAccess(
                Mockito.any(UserId.class),
                Mockito.any(CloudAccessClientId.class)
        );
    }*/
}