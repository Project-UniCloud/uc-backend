package com.unicloudapp.user.infrastructure.rest;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.application.UserDTO;
import com.unicloudapp.user.application.UserDomainDtoMapper;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.ports.in.CreateLecturerUseCase;
import com.unicloudapp.user.application.ports.in.CreateStudentUseCase;
import com.unicloudapp.user.application.ports.in.FindUserUseCase;
import com.unicloudapp.user.application.ports.out.AuthenticationPort;
import com.unicloudapp.user.application.ports.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import com.unicloudapp.user.domain.UserRole;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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

import java.time.LocalDate;
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

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @MockitoBean
    private AuthenticationPort authenticationPort;

    @MockitoBean
    private UserDomainDtoMapper userDomainDtoMapper;

    @Autowired
    private UserRestController userRestController;

    @MockitoBean
    private CreateStudentUseCase createStudentUseCase;

    @MockitoBean
    private CreateLecturerUseCase createLecturerUseCase;

    @MockitoBean
    private FindUserUseCase findUserUseCase;

    /**
     * Test {@link UserRestController#authenticate(AuthenticateRequest)}.
     * <p>
     * Method under test: {@link UserRestController#authenticate(AuthenticateRequest)}
     */
    @Test
    @DisplayName("Test authenticate(AuthenticateRequest)")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity UserRestController.authenticate(AuthenticateRequest)"})
    void testAuthenticate() throws Exception {
        // Arrange
        doNothing().when(authenticationPort)
                .authenticate(Mockito.any(),
                        Mockito.any()
                );
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/users/auth")
                .contentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(new AuthenticateRequest("Login",
                        "iloveyou"
                )));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());
    }

    /**
     * Test {@link UserRestController#createStudent(CreateLecturerRequest)} with {@code createLecturerRequest}.
     * <p>
     * Method under test: {@link UserRestController#createStudent(CreateLecturerRequest)}
     */
    @Test
    @DisplayName("Test createLecturer(CreateLecturerRequest) with 'createLecturerRequest'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"CreatedLecturerResponse UserRestController.createLecturer(CreateLecturerRequest)"})
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
    @MethodsUnderTest({"CreatedLecturerResponse UserRestController.createLecturer(CreateLecturerRequest)"})
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
        UserRestController userRestController = new UserRestController(mock(AuthenticationPort.class),
                createLecturerUseCase,
                createStudentUseCase,
                findUserUseCase,
                mock(UserDomainDtoMapper.class)
        );

        // Act
        CreatedLecturerResponse actualCreateLecturerResult = userRestController
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
    @MethodsUnderTest({"CreatedStudentResponse UserRestController.createLecturer(CreateStudentRequest)"})
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
    @MethodsUnderTest({"CreatedStudentResponse UserRestController.createLecturer(CreateStudentRequest)"})
    void testCreateLecturerWithRequest_thenReturnStudentIdIsRandomUUID() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        UserId userId = mock(UserId.class);
        UUID randomUUIDResult = UUID.randomUUID();
        when(userId.getValue()).thenReturn(randomUUIDResult);
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(userId);
        when(createStudentUseCase.createStudent(Mockito.any())).thenReturn(user);
        UserRestController userRestController = new UserRestController(mock(AuthenticationPort.class),
                createLecturerUseCase,
                createStudentUseCase,
                findUserUseCase,
                mock(UserDomainDtoMapper.class)
        );

        // Act
        CreatedStudentResponse actualCreateLecturerResult = userRestController
                .createStudent(new CreateStudentRequest("42",
                        "Jane",
                        "Doe"
                ));

        // Assert
        verify(userId).getValue();
        verify(createStudentUseCase).createStudent(isA(CreateStudentCommand.class));
        verify(user).getUserId();
        assertSame(randomUUIDResult,
                actualCreateLecturerResult.studentId()
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
    @MethodsUnderTest({"UserDTO UserRestController.getUserById(UUID)"})
    void testGetUserById() throws Exception {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        var randomUUIDResult = UUID.randomUUID();
        when(findUserUseCase.findUserById(Mockito.any())).thenReturn(mock(User.class));
        UserDTO.UserDTOBuilder firstNameResult = UserDTO.builder()
                .email("jane.doe@example.org")
                .firstName("Jane");
        UserDTO.UserDTOBuilder loginResult = firstNameResult.lastLoginAt(LocalDate.of(1970,
                                1,
                                1
                        )
                        .atStartOfDay())
                .lastName("Doe")
                .login("Login");
        UserDTO buildResult = loginResult.userId(randomUUIDResult)
                .userRole(UserRole.Type.ADMIN)
                .build();
        when(userDomainDtoMapper.toDto(Mockito.any())).thenReturn(buildResult);
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
                                        + ",\"userRole\":\"ADMIN\",\"email\":\"jane.doe@example.org\"," +
                                        "\"lastLoginAt\":[1970,1,1,0,0]}", randomUUIDResult)));
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
    @MethodsUnderTest({"UserDTO UserRestController.getUserById(UUID)"})
    void testGetUserById_givenUserRepositoryPortFindByIdReturnOfUser_thenCallsFindById() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
        Optional<User> ofResult = Optional.of(mock(User.class));
        when(userRepository.findById(Mockito.any())).thenReturn(ofResult);
        UserDomainDtoMapper userDomainDtoMapper = mock(UserDomainDtoMapper.class);
        UserDTO.UserDTOBuilder firstNameResult = UserDTO.builder()
                .email("jane.doe@example.org")
                .firstName("Jane");
        LocalDate ofResult2 = LocalDate.of(1970,
                1,
                1
        );
        UserDTO.UserDTOBuilder loginResult = firstNameResult.lastLoginAt(ofResult2.atStartOfDay())
                .lastName("Doe")
                .login("Login");
        UUID userId = UUID.randomUUID();
        UserDTO buildResult = loginResult.userId(userId)
                .userRole(UserRole.Type.ADMIN)
                .build();
        when(userDomainDtoMapper.toDto(Mockito.any())).thenReturn(buildResult);
        UserRestController userRestController = new UserRestController(mock(AuthenticationPort.class),
                createLecturerUseCase,
                createStudentUseCase,
                findUserUseCase,
                userDomainDtoMapper
        );

        // Act
        UserDTO actualUserById = userRestController.getUserById(UUID.randomUUID());

        // Assert
        verify(userDomainDtoMapper).toDto(isA(User.class));
        verify(userRepository).findById(isA(UserId.class));
        LocalDateTime lastLoginAt = actualUserById.lastLoginAt();
        assertEquals("00:00",
                lastLoginAt.toLocalTime()
                        .toString()
        );
        LocalDate toLocalDateResult = lastLoginAt.toLocalDate();
        assertEquals("1970-01-01",
                toLocalDateResult.toString()
        );
        assertEquals("Doe",
                actualUserById.lastName()
        );
        assertEquals("Jane",
                actualUserById.firstName()
        );
        assertEquals("Login",
                actualUserById.login()
        );
        assertEquals("jane.doe@example.org",
                actualUserById.email()
        );
        assertEquals(UserRole.Type.ADMIN,
                actualUserById.userRole()
        );
        assertSame(ofResult2,
                toLocalDateResult
        );
        assertSame(userId,
                actualUserById.userId()
        );
    }

    @Test
    @DisplayName("Test getUserById(UUID); given UserService findUserById(UserId) return 'null'; then calls findUserById(UserId)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"UserDTO UserRestController.getUserById(UUID)"})
    void testGetUserById_givenUserServiceFindUserByIdReturnNull_thenCallsFindUserById() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        when(findUserUseCase.findUserById(Mockito.any())).thenReturn(null);
        UserDomainDtoMapper userDomainDtoMapper = mock(UserDomainDtoMapper.class);
        UserDTO.UserDTOBuilder firstNameResult = UserDTO.builder()
                .email("jane.doe@example.org")
                .firstName("Jane");
        LocalDate ofResult = LocalDate.of(1970,
                1,
                1
        );
        UserDTO.UserDTOBuilder loginResult = firstNameResult.lastLoginAt(ofResult.atStartOfDay())
                .lastName("Doe")
                .login("Login");
        UUID userId = UUID.randomUUID();
        UserDTO buildResult = loginResult.userId(userId)
                .userRole(UserRole.Type.ADMIN)
                .build();
        when(userDomainDtoMapper.toDto(Mockito.any())).thenReturn(buildResult);
        UserRestController userRestController = new UserRestController(mock(AuthenticationPort.class),
                createLecturerUseCase,
                createStudentUseCase,
                findUserUseCase,
                userDomainDtoMapper
        );

        // Act
        UserDTO actualUserById = userRestController.getUserById(UUID.randomUUID());

        // Assert
        verify(userDomainDtoMapper).toDto(isNull());
        verify(findUserUseCase).findUserById(isA(UserId.class));
        LocalDateTime lastLoginAt = actualUserById.lastLoginAt();
        assertEquals("00:00",
                lastLoginAt.toLocalTime()
                        .toString()
        );
        LocalDate toLocalDateResult = lastLoginAt.toLocalDate();
        assertEquals("1970-01-01",
                toLocalDateResult.toString()
        );
        assertEquals("Doe",
                actualUserById.lastName()
        );
        assertEquals("Jane",
                actualUserById.firstName()
        );
        assertEquals("Login",
                actualUserById.login()
        );
        assertEquals("jane.doe@example.org",
                actualUserById.email()
        );
        assertEquals(UserRole.Type.ADMIN,
                actualUserById.userRole()
        );
        assertSame(ofResult,
                toLocalDateResult
        );
        assertSame(userId,
                actualUserById.userId()
        );
    }
}

