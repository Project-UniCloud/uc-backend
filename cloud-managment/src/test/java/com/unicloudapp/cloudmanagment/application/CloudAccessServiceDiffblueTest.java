package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

class CloudAccessServiceDiffblueTest {

    /**
     * Test {@link CloudAccessService#giveCloudAccess(UserId, CloudAccessClientId)}.
     * <ul>
     *   <li>When {@link CloudAccessClientId} with accessId is {@code 42}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloudAccessService#giveCloudAccess(UserId, CloudAccessClientId)}
     */
    @Test
    @DisplayName("Test giveCloudAccess(UserId, CloudAccessClientId); when CloudAccessClientId with accessId is '42'")
    void testGiveCloudAccess_whenCloudAccessClientIdWithAccessIdIs42() {

        // Arrange
        CloudAccessService cloudAccessService = new CloudAccessService(
                new HashMap<>(
                        Map.of("42", Mockito.mock(CloudAccessClient.class))
                ),
                Mockito.mock(CloudAccessRepositoryPort.class)
        );

        // Act
        cloudAccessService.giveCloudAccess(null,
                CloudAccessClientId.of("42")
        );
    }
}
