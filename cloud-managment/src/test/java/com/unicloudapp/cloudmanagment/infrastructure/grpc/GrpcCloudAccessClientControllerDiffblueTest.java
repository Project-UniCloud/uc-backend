package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.cloudmanagment.domain.CloudAccessId;
import com.unicloudapp.cloudmanagment.domain.ExternalUserId;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcCloudAccessClientControllerDiffblueTest {

    @Mock
    private ManagedChannel channel;

    @Mock
    private CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    @Test
    @DisplayName("Test new GrpcCloudAccessClientController(String, int)")
    @Tag("MaintainedByDiffblue")
    void testNewGrpcCloudAccessClientController() {
        // Arrange
        // Act
        new GrpcCloudAccessClientController(channel, stub);
    }

    /**
     * Test {@link GrpcCloudAccessClientController#shutdown()}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#shutdown()}
     */
    @Test
    @DisplayName("Test shutdown()")
    @Tag("MaintainedByDiffblue")
    void testShutdown() {
        // Arrange
        // Act
        GrpcCloudAccessClientController actualGrpcCloudAccessClientController = new GrpcCloudAccessClientController(
                channel, stub
        );
        actualGrpcCloudAccessClientController.shutdown();

        // Assert
        verify(channel).shutdown();
    }

    /**
     * Test {@link GrpcCloudAccessClientController#giveCloudAccess(UserId, CloudAccessClientId)}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#giveCloudAccess(UserId, CloudAccessClientId)}
     */
    @Test
    @DisplayName("Test giveCloudAccess(UserId, CloudAccessClientId)")
    @Tag("MaintainedByDiffblue")
    void testGiveCloudAccess() {
        // Arrange
        AdapterInterface.UserCreatedResponse response = AdapterInterface.UserCreatedResponse
                .newBuilder()
                .setId("42")
                .build();
        var userId = UserId.of(UUID.randomUUID());
        var cloudAccessClientId = CloudAccessClientId.of("aws");
        when(stub.createUser(any())).thenReturn(response);
        CloudAccess expectedResult = CloudAccess.builder()
                .cloudAccessId(CloudAccessId.of(UUID.randomUUID()))
                .cloudAccessClientId(cloudAccessClientId)
                .externalUserId(ExternalUserId.of(response.getId()))
                .userId(userId)
                .build();

        // Act
        CloudAccess result = new GrpcCloudAccessClientController(
                channel,
                stub
        ).giveCloudAccess(userId, cloudAccessClientId);

        // Assert
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("cloudAccessId")
                .isEqualTo(expectedResult);
    }

    /**
     * Test {@link GrpcCloudAccessClientController#isRunning()}.
     * <p>
     * Method under test: {@link GrpcCloudAccessClientController#isRunning()}
     */
    @Test
    @DisplayName("Test isRunning()")
    @Tag("MaintainedByDiffblue")
    void testIsRunning() {
        // Arrange
        AdapterInterface.StatusRequest request = AdapterInterface.StatusRequest
                .newBuilder()
                .build();
        AdapterInterface.StatusResponse statusResponse = Mockito.mock(AdapterInterface.StatusResponse.class);
        when(statusResponse.getIsHealthy()).thenReturn(true);
        when(stub.getStatus(request)).thenReturn(statusResponse);

        // Act
        boolean actualIsRunningResult = new GrpcCloudAccessClientController(
                channel,
                stub
        ).isRunning();

        // Assert
        assertThat(actualIsRunningResult).isTrue();
    }
}
