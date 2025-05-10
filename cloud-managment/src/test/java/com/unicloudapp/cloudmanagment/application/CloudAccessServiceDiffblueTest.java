package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;

import java.util.HashMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
    @Disabled("TODO: Complete this test")
    void testGiveCloudAccess_whenCloudAccessClientIdWithAccessIdIs42() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.unicloudapp.cloudmanagment.domain.CloudAccessClient.giveCloudAccess(com.unicloudapp.common.domain.user.UserId, com.unicloudapp.common.domain.cloud.CloudAccessClientId)" because "cloudAccessClient" is null
        //       at com.unicloudapp.cloudmanagment.application.CloudAccessService.giveCloudAccess(CloudAccessService.java:47)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        CloudAccessService cloudAccessService = new CloudAccessService(new CloudAccessClientProperties(new HashMap<>()),
                null,
                null
        );

        // Act
        cloudAccessService.giveCloudAccess(null,
                CloudAccessClientId.of("42")
        );
    }
}
