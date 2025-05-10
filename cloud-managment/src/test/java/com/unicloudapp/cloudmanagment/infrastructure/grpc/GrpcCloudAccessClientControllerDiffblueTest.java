package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class GrpcCloudAccessClientControllerDiffblueTest {

    /**
     * Test {@link GrpcCloudAccessClientController#GrpcCloudAccessClientController(String, int)}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#GrpcCloudAccessClientController(String, int)}
     */
    @Test
    @DisplayName("Test new GrpcCloudAccessClientController(String, int)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testNewGrpcCloudAccessClientController() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        String host = "";
        int port = 0;

        // Act
        GrpcCloudAccessClientController actualGrpcCloudAccessClientController = new GrpcCloudAccessClientController(host,
                port
        );

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link GrpcCloudAccessClientController#shutdown()}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#shutdown()}
     */
    @Test
    @DisplayName("Test shutdown()")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testShutdown() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        GrpcCloudAccessClientController grpcCloudAccessClientController = null;

        // Act
        grpcCloudAccessClientController.shutdown();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link GrpcCloudAccessClientController#giveCloudAccess(UserId, CloudAccessClientId)}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#giveCloudAccess(UserId, CloudAccessClientId)}
     */
    @Test
    @DisplayName("Test giveCloudAccess(UserId, CloudAccessClientId)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testGiveCloudAccess() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        GrpcCloudAccessClientController grpcCloudAccessClientController = null;
        UserId userId = null;
        CloudAccessClientId cloudAccessClientId = null;

        // Act
        CloudAccess actualGiveCloudAccessResult = grpcCloudAccessClientController.giveCloudAccess(userId,
                cloudAccessClientId
        );

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link GrpcCloudAccessClientController#isRunning()}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#isRunning()}
     */
    @Test
    @DisplayName("Test isRunning()")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testIsRunning() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        GrpcCloudAccessClientController grpcCloudAccessClientController = null;

        // Act
        boolean actualIsRunningResult = grpcCloudAccessClientController.isRunning();

        // Assert
        // TODO: Add assertions on result
    }
}
