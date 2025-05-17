package com.unicloudapp.user.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.port.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.port.in.CreateStudentUseCase;
import com.unicloudapp.user.application.port.in.FindUserUseCase;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UserRestController.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class UserRestControllerDiffblueTest {

    @MockitoBean
    private UserToUserFoundResponseMapper userDomainDtoMapper;

    @Autowired
    private UserRestController userRestController;

    @MockitoBean
    private CreateStudentUseCase createStudentUseCase;

    @MockitoBean
    private CreateLecturerUseCase createLecturerUseCase;

    @MockitoBean
    private FindUserUseCase findUserUseCase;

    /**
     * Test {@link UserRestController#createStudent(CreateLecturerRequest)} with {@code createLecturerRequest}.
     * <p>
     * Method under test: {@link UserRestController#createStudent(CreateLecturerRequest)}
     */
    @Test
    @DisplayName("Test createLecturer(CreateLecturerRequest) with 'createLecturerRequest'")
    @Tag("MaintainedByDiffblue")
    void testCreateLecturerWithCreateLecturerRequest() throws Exception {
        // Arrange
        User user = mock(User.class);
        UserId userId = mock(UserId.class);
        UUID uuid = UUID.randomUUID();
        when(userId.getValue()).thenReturn(uuid);
        when(user.getUserId()).thenReturn(userId);
        when(createLecturerUseCase.createLecturer(Mockito.any())).thenReturn(user);
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/users/lecturers")
                .contentType(MediaType.APPLICATION_JSON);
        CreateLecturerRequest createLecturerRequest = new CreateLecturerRequest("42",
                "Jane",
                "Doe"
        );

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content((new ObjectMapper()).writeValueAsString(createLecturerRequest));

        // Act and Assert
        var result = String.format("{\"lecturerId\":\"%s\"}",
                uuid
        );
        MockMvcBuilders.standaloneSetup(userRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(result));
    }

    /**
     * Test {@link UserRestController#createStudent(CreateLecturerRequest)} with {@code createLecturerRequest}.
     * <ul>
     *   <li>Then return lecturerId is randomUUID.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserRestController#createStudent(CreateLecturerRequest)}
     */
    @Test
    @DisplayName("Test createLecturer(CreateLecturerRequest) with 'createLecturerRequest'; then return lecturerId is randomUUID")
    @Tag("MaintainedByDiffblue")
    void testCreateLecturerWithCreateLecturerRequest_thenReturnLecturerIdIsRandomUUID() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        UserId userId = mock(UserId.class);
        UUID randomUUIDResult = UUID.randomUUID();
        when(userId.getValue()).thenReturn(randomUUIDResult);
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(userId);
        when(createLecturerUseCase.createLecturer(Mockito.any())).thenReturn(user);
        UserRestController userRestController = new UserRestController(
                createLecturerUseCase,
                createStudentUseCase,
                findUserUseCase,
                userDomainDtoMapper
        );

        // Act
        LecturerCreatedResponse actualCreateLecturerResult = userRestController
                .createStudent(new CreateLecturerRequest("42",
                        "Jane",
                        "Doe"
                ));

        // Assert
        verify(userId).getValue();
        verify(createLecturerUseCase).createLecturer(isA(CreateLecturerCommand.class));
        verify(user).getUserId();
        assertSame(randomUUIDResult,
                actualCreateLecturerResult.lecturerId()
        );
    }

    /**
     * Test {@link UserRestController#createStudent(CreateStudentRequest)} with {@code request}.
     * <p>
     * Method under test: {@link UserRestController#createStudent(CreateStudentRequest)}
     */
    @Test
    @DisplayName("Test createLecturer(CreateStudentRequest) with 'request'")
    @Tag("MaintainedByDiffblue")
    void testCreateLecturerWithRequest() throws Exception {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        UUID randomUUIDResult = UUID.randomUUID();
        User user = mock(User.class);
        UserId userId = mock(UserId.class);
        when(userId.getValue()).thenReturn(randomUUIDResult);
        when(user.getUserId()).thenReturn(userId);
        when(createStudentUseCase.createStudent(Mockito.any())).thenReturn(user);
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/users/students")
                .contentType(MediaType.APPLICATION_JSON);
        CreateStudentRequest createStudentRequest = new CreateStudentRequest("42",
                "Jane",
                "Doe"
        );

        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content((new ObjectMapper()).writeValueAsString(createStudentRequest));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format("{\"studentId\":\"%s\"}", randomUUIDResult)));
    }

    /**
     * Test {@link UserRestController#createStudent(CreateStudentRequest)} with {@code request}.
     * <ul>
     *   <li>Then return studentId is randomUUID.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserRestController#createStudent(CreateStudentRequest)}
     */
    @Test
    @DisplayName("Test createLecturer(CreateStudentRequest) with 'request'; then return studentId is randomUUID")
    @Tag("MaintainedByDiffblue")
    void testCreateLecturerWithRequest_thenReturnStudentIdIsRandomUUID() {
        // Arrange
        UserId userId = mock(UserId.class);
        UUID randomUUIDResult = UUID.randomUUID();
        when(userId.getValue()).thenReturn(randomUUIDResult);
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(userId);
        when(createStudentUseCase.createStudent(Mockito.any())).thenReturn(user);
        UserRestController userRestController = new UserRestController(
                createLecturerUseCase,
                createStudentUseCase,
                findUserUseCase,
                userDomainDtoMapper
        );

        // Act
        StudentCreatedResponse studentCreatedResponse = userRestController
                .createStudent(new CreateStudentRequest("42",
                        "Jane",
                        "Doe"
                ));

        // Assert
        verify(userId).getValue();
        verify(createStudentUseCase).createStudent(isA(CreateStudentCommand.class));
        verify(user).getUserId();
        assertSame(randomUUIDResult,
                studentCreatedResponse.studentId()
        );
    }

    /**
     * Test {@link UserRestController#getUserById(UUID)}.
     * <p>
     * Method under test: {@link UserRestController#getUserById(UUID)}
     */
    @Test
    @DisplayName("Test getUserById(UUID)")
    @Tag("MaintainedByDiffblue")
    void testGetUserById() throws Exception {
        // Arrange
        var randomUUIDResult = UUID.randomUUID();
        when(findUserUseCase.findUserById(Mockito.any())).thenReturn(mock(User.class));
        UserFoundResponse buildResult = UserFoundResponse.builder()
                .userId(randomUUIDResult)
                .userRole(UserRole.Type.ADMIN)
                .login("Login")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.org")
                .lastLoginAt(LocalDateTime.of(1970, 1, 1, 0, 0))
                .userRole(UserRole.Type.ADMIN)
                .build();
        when(userDomainDtoMapper.toUserFoundResponse(Mockito.any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/{userId}",
                randomUUIDResult
        );

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(
                                "{\"userId\":\"%s\",\"login\":\"Login\",\"firstName\":\"Jane\",\"lastName\":\"Doe\""
                                        + ",\"email\":\"jane.doe@example.org\"," +
                                        "\"lastLoginAt\":[1970,1,1,0,0],\"userRole\":\"ADMIN\"}", randomUUIDResult)));
    }

    /**
     * Test {@link UserRestController#getUserById(UUID)}.
     * <ul>
     *   <li>Given {@link UserRepositoryPort} {@link UserRepositoryPort#findById(UserId)} return of {@link User}.</li>
     *   <li>Then calls {@link UserRepositoryPort#findById(UserId)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserRestController#getUserById(UUID)}
     */
    @Test
    @DisplayName("Test getUserById(UUID); given UserRepositoryPort findById(UserId) return of User; then calls findById(UserId)")
    @Tag("MaintainedByDiffblue")
    void testGetUserById_givenUserRepositoryPortFindByIdReturnOfUser_thenCallsFindById() {
        // Arrange
        UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
        Optional<User> ofResult = Optional.of(mock(User.class));
        when(userRepository.findById(Mockito.any())).thenReturn(ofResult);
        UUID userId = UUID.randomUUID();
        var lastLogin = LocalDateTime.now();
        UserFoundResponse buildResult = UserFoundResponse.builder()
                .userId(userId)
                .userRole(UserRole.Type.ADMIN)
                .lastLoginAt(lastLogin)
                .firstName("Jane")
                .lastName("Doe")
                .login("Login")
                .email("jane.doe@example.org")
                .build();
        when(userDomainDtoMapper.toUserFoundResponse(Mockito.any())).thenReturn(buildResult);
        when(findUserUseCase.findUserById(Mockito.any())).thenReturn(mock(User.class));

        // Act
        UserFoundResponse actualUserById = userRestController.getUserById(UUID.randomUUID());

        // Assert
        verify(userDomainDtoMapper).toUserFoundResponse(isA(User.class));
        LocalDateTime lastLoginAt = actualUserById.lastLoginAt();
        assertEquals(lastLogin, lastLoginAt);
        assertEquals("Doe", actualUserById.lastName());
        assertEquals("Jane", actualUserById.firstName());
        assertEquals("Login", actualUserById.login());
        assertEquals("jane.doe@example.org", actualUserById.email());
        assertEquals(UserRole.Type.ADMIN, actualUserById.userRole());
        assertSame(userId, actualUserById.userId());
    }

    @Test
    @DisplayName("Test getUserById(UUID); given UserService findUserById(UserId) return 'null'; then calls findUserById(UserId)")
    @Tag("MaintainedByDiffblue")
    void testGetUserById_givenUserServiceFindUserByIdReturnNull_thenCallsFindUserById() {
        // Arrange
        when(findUserUseCase.findUserById(Mockito.any())).thenReturn(null);
        UUID userId = UUID.randomUUID();
        var lastLogin = LocalDateTime.now();
        UserFoundResponse buildResult = UserFoundResponse.builder()
                .userId(userId)
                .userRole(UserRole.Type.ADMIN)
                .lastLoginAt(lastLogin)
                .firstName("Jane")
                .lastName("Doe")
                .login("Login")
                .email("jane.doe@example.org")
                .build();
        when(userDomainDtoMapper.toUserFoundResponse(Mockito.any())).thenReturn(buildResult);

        // Act
        UserFoundResponse actualUserById = userRestController.getUserById(UUID.randomUUID());

        // Assert
        verify(userDomainDtoMapper).toUserFoundResponse(isNull());
        verify(findUserUseCase).findUserById(isA(UserId.class));
        LocalDateTime lastLoginAt = actualUserById.lastLoginAt();
        assertEquals(lastLogin, actualUserById.lastLoginAt());
        assertEquals(lastLogin, lastLoginAt);
        assertEquals("Doe", actualUserById.lastName());
        assertEquals("Jane", actualUserById.firstName());
        assertEquals("Login", actualUserById.login());
        assertEquals("jane.doe@example.org", actualUserById.email());
        assertEquals(UserRole.Type.ADMIN, actualUserById.userRole());
        assertSame(userId, actualUserById.userId());
    }
}

