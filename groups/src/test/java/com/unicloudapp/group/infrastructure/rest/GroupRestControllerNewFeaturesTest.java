package com.unicloudapp.group.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.application.port.StudentImporterPort;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class GroupRestControllerNewFeaturesTest {

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("getAllGroupsByStatus throws when resourceType provided but cloudClientId missing")
    void getAllGroupsByStatus_validation() {
        GroupService groupService = mock(GroupService.class);
        StudentImporterPort importer = mock(StudentImporterPort.class);
        UserQueryService userQueryService = mock(UserQueryService.class);
        GroupRestController controller = new GroupRestController(groupService, importer, userQueryService);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.getAllGroupsByStatus(
                        0, 10, null, null, null, CloudResourceType.of("S3").toString()));
        assertTrue(ex.getMessage().contains("Cloud client id is required"));
    }

    @Test
    @DisplayName("importStudents parses CSV and delegates to service")
    void importStudents_parsesAndDelegates() throws IOException {
        GroupService groupService = mock(GroupService.class);
        StudentImporterPort importer = mock(StudentImporterPort.class);
        UserQueryService userQueryService = mock(UserQueryService.class);
        GroupRestController controller = new GroupRestController(groupService, importer, userQueryService);

        UUID groupId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                "login,firstName,lastName,email\njsmith,John,Smith,john@ex.com\n".getBytes());

        StudentBasicData s = StudentBasicData.builder()
                .login("jsmith")
                .firstName("John")
                .lastName("Smith")
                .email("john@ex.com")
                .build();
        when(importer.parseCsv(any())).thenReturn(List.of(s));

        controller.importStudents(groupId, file);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StudentBasicData>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(importer).parseCsv(file);
        verify(groupService).addStudents(eq(GroupId.of(groupId)), listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
        assertEquals("jsmith", listCaptor.getValue().getFirst().getLogin());
    }
}
