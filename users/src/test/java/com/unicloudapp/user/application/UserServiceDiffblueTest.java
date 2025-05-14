package com.unicloudapp.user.application;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.application.UserDTO.UserDTOBuilder;
import com.unicloudapp.user.application.command.CreateLecturerCommand;
import com.unicloudapp.user.application.command.CreateStudentCommand;
import com.unicloudapp.user.application.ports.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import com.unicloudapp.user.domain.UserRole.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = UserService.class)
@ExtendWith(SpringExtension.class)
class UserServiceDiffblueTest {

    @MockitoBean
    private UserFactory userFactory;

    @MockitoBean
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Test createLecturer(UserDTO); given UserRepositoryPort save(User) throw IllegalStateException(String) with 'foo'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.createLecturer(UserDTO)"})
    void testCreateLecturer_givenUserRepositoryPortSaveThrowIllegalStateExceptionWithFoo() {
        // Arrange
        when(userRepositoryPort.save(Mockito.<User>any())).thenThrow(new IllegalStateException("foo"));
        when(userFactory.create(Mockito.<UUID>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<Type>any()
        )).thenReturn(null);
        CreateLecturerCommand userDTO = CreateLecturerCommand.builder()
                .login("42")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> userService.createLecturer(userDTO)
        );
        verify(userRepositoryPort).save(isNull());
        verify(userFactory).create(isA(UUID.class),
                eq("42"),
                eq("Jane"),
                eq("Doe"),
                eq(Type.LECTURER)
        );
    }

    @Test
    @DisplayName("Test createLecturer(UserDTO); given UserRepositoryPort; then throw IllegalStateException")
    @MethodsUnderTest({"User UserService.createLecturer(UserDTO)"})
    void testCreateLecturer_givenUserRepositoryPort_thenThrowIllegalStateException() {
        // Arrange
        when(userFactory.create(Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenThrow(new IllegalStateException("foo"));

        // Act and Assert
        CreateLecturerCommand userDTO = CreateLecturerCommand.builder()
                .login("42")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        assertThrows(
                IllegalStateException.class,
                () -> userService.createLecturer(userDTO)
        );
        verify(userFactory).create(isA(UUID.class),
                eq("42"),
                eq("Jane"),
                eq("Doe"),
                eq(Type.LECTURER)
        );
    }

    @Test
    @DisplayName("Test createStudent(UserDTO)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.createStudent(UserDTO)"})
    void testCreateStudent() {
        // Arrange
        UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(null);
        UserService userService = new UserService(userRepository,
                new UserFactory()
        );

        // Act
        var userDto = CreateStudentCommand.builder()
                .login("login")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        User actualCreateStudentResult = userService
                .createStudent(userDto);

        // Assert
        verify(userRepository).save(isA(User.class));
        assertNull(actualCreateStudentResult);
    }

    @Test
    @DisplayName("Test createStudent(UserDTO); given UserRepositoryPort save(User) return 'null'; then return 'null'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.createStudent(UserDTO)"})
    void testCreateStudent_givenUserRepositoryPortSaveReturnNull_thenReturnNull() {
        // Arrange
        when(userRepositoryPort.save(Mockito.<User>any())).thenReturn(null);
        when(userFactory.create(Mockito.<UUID>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<Type>any()
        )).thenReturn(null);

        // Act
        CreateStudentCommand createStudentCommand = CreateStudentCommand.builder()
                .login("42")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        User actualCreateStudentResult = userService
                .createStudent(createStudentCommand);

        // Assert
        verify(userRepositoryPort).save(isNull());
        verify(userFactory).create(isA(UUID.class),
                eq("login"),
                eq("Jane"),
                eq("Doe"),
                eq(Type.STUDENT)
        );
        assertNull(actualCreateStudentResult);
    }

    @Test
    @DisplayName("Test createStudent(UserDTO); given UserRepositoryPort save(User) throw IllegalStateException(String) with 'foo'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.createStudent(UserDTO)"})
    void testCreateStudent_givenUserRepositoryPortSaveThrowIllegalStateExceptionWithFoo() {
        // Arrange
        when(userRepositoryPort.save(Mockito.<User>any())).thenThrow(new IllegalStateException("foo"));
        when(userFactory.create(Mockito.<UUID>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<Type>any()
        )).thenReturn(null);
        CreateStudentCommand createStudentCommand = CreateStudentCommand.builder()
                .login("42")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> userService.createStudent(createStudentCommand)
        );
        verify(userRepositoryPort).save(isNull());
        verify(userFactory).create(isA(UUID.class),
                eq("42"),
                eq("Jane"),
                eq("Doe"),
                eq(Type.STUDENT)
        );
    }

    @Test
    @DisplayName("Test createStudent(UserDTO); given UserRepositoryPort; then throw IllegalStateException")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.createStudent(UserDTO)"})
    void testCreateStudent_givenUserRepositoryPort_thenThrowIllegalStateException() {
        // Arrange
        when(userFactory.create(Mockito.<UUID>any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenThrow(new IllegalStateException("foo"));

        // Act and Assert
        CreateStudentCommand createStudentCommand = CreateStudentCommand.builder()
                .login("42")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        assertThrows(IllegalStateException.class,
                () -> userService.createStudent(createStudentCommand)
        );
        verify(userFactory).create(isA(UUID.class),
                eq("42"),
                eq("Jane"),
                eq("Doe"),
                eq(Type.STUDENT)
        );
    }

    /**
     * Test {@link UserService#updateFirstName(UserId, FirstName)}.
     * <ul>
     *   <li>Given {@link UserRepositoryPort} {@link UserRepositoryPort#findById(UserId)} return empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#updateFirstName(UserId, FirstName)}
     */
    @Test
    @DisplayName("Test updateFirstName(UserId, FirstName); given UserRepositoryPort findById(UserId) return empty")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void UserService.updateFirstName(UserId, FirstName)"})
    void testUpdateFirstName_givenUserRepositoryPortFindByIdReturnEmpty() throws IllegalArgumentException {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(userRepositoryPort.findById(Mockito.<UserId>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> userService.updateFirstName(null,
                        FirstName.of("42")
                )
        );
        verify(userRepositoryPort).findById(isNull());
    }

    /**
     * Test {@link UserService#updateFirstName(UserId, FirstName)}.
     * <ul>
     *   <li>Given {@link User} {@link User#updateFirstName(FirstName)} does nothing.</li>
     *   <li>Then calls {@link User#updateFirstName(FirstName)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#updateFirstName(UserId, FirstName)}
     */
    @Test
    @DisplayName("Test updateFirstName(UserId, FirstName); given User updateFirstName(FirstName) does nothing; then calls updateFirstName(FirstName)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void UserService.updateFirstName(UserId, FirstName)"})
    void testUpdateFirstName_givenUserUpdateFirstNameDoesNothing_thenCallsUpdateFirstName()
            throws IllegalArgumentException {
        // Arrange
        User user = mock(User.class);
        doNothing().when(user)
                .updateFirstName(Mockito.<FirstName>any());
        Optional<User> ofResult = Optional.of(user);
        when(userRepositoryPort.findById(Mockito.<UserId>any())).thenReturn(ofResult);

        // Act
        userService.updateFirstName(null,
                FirstName.of("42")
        );

        // Assert
        verify(userRepositoryPort).findById(isNull());
        verify(user).updateFirstName(isA(FirstName.class));
    }

    /**
     * Test {@link UserService#updateFirstName(UserId, FirstName)}.
     * <ul>
     *   <li>Given {@link User} {@link User#updateFirstName(FirstName)} throw {@link IllegalStateException#IllegalStateException(String)} with {@code foo}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#updateFirstName(UserId, FirstName)}
     */
    @Test
    @DisplayName("Test updateFirstName(UserId, FirstName); given User updateFirstName(FirstName) throw IllegalStateException(String) with 'foo'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void UserService.updateFirstName(UserId, FirstName)"})
    void testUpdateFirstName_givenUserUpdateFirstNameThrowIllegalStateExceptionWithFoo() throws IllegalArgumentException {
        // Arrange
        User user = mock(User.class);
        doThrow(new IllegalStateException("foo")).when(user)
                .updateFirstName(Mockito.<FirstName>any());
        Optional<User> ofResult = Optional.of(user);
        when(userRepositoryPort.findById(Mockito.<UserId>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> userService.updateFirstName(null,
                        FirstName.of("42")
                )
        );
        verify(userRepositoryPort).findById(isNull());
        verify(user).updateFirstName(isA(FirstName.class));
    }

    /**
     * Test {@link UserService#findUserById(UserId)}.
     * <ul>
     *   <li>Given {@link UserRepositoryPort} {@link UserRepositoryPort#findById(UserId)} return of {@link User}.</li>
     *   <li>Then calls {@link UserRepositoryPort#findById(UserId)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#findUserById(UserId)}
     */
    @Test
    @DisplayName("Test findUserById(UserId); given UserRepositoryPort findById(UserId) return of User; then calls findById(UserId)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.findUserById(UserId)"})
    void testFindUserById_givenUserRepositoryPortFindByIdReturnOfUser_thenCallsFindById() {
        // Arrange
        Optional<User> ofResult = Optional.of(mock(User.class));
        when(userRepositoryPort.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        userService.findUserById(null);

        // Assert
        verify(userRepositoryPort).findById(isNull());
    }

    /**
     * Test {@link UserService#findUserById(UserId)}.
     * <ul>
     *   <li>Given {@link UserRepositoryPort} {@link UserRepositoryPort#findById(UserId)} return of {@link User}.</li>
     *   <li>Then calls {@link UserRepositoryPort#findById(UserId)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#findUserById(UserId)}
     */
    @Test
    @DisplayName("Test findUserById(UserId); given UserRepositoryPort findById(UserId) return of User; then calls findById(UserId)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.findUserById(UserId)"})
    void testFindUserById_givenUserRepositoryPortFindByIdReturnOfUser_thenCallsFindById2() {
        // Arrange
        Optional<User> ofResult = Optional.of(mock(User.class));
        when(userRepositoryPort.findById(Mockito.<UserId>any())).thenReturn(ofResult);

        // Act
        userService.findUserById(null);

        // Assert
        verify(userRepositoryPort).findById(isNull());
    }

    /**
     * Test {@link UserService#findUserById(UserId)}.
     * <ul>
     *   <li>Then throw {@link IllegalStateException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#findUserById(UserId)}
     */
    @Test
    @DisplayName("Test findUserById(UserId); then throw IllegalStateException")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.findUserById(UserId)"})
    void testFindUserById_thenThrowIllegalStateException() {
        // Arrange
        when(userRepositoryPort.findById(Mockito.any())).thenThrow(new IllegalStateException("foo"));

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> userService.findUserById(null)
        );
        verify(userRepositoryPort).findById(isNull());
    }

    /**
     * Test {@link UserService#findUserById(UserId)}.
     * <ul>
     *   <li>Then throw {@link IllegalStateException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserService#findUserById(UserId)}
     */
    @Test
    @DisplayName("Test findUserById(UserId); then throw IllegalStateException")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"User UserService.findUserById(UserId)"})
    void testFindUserById_thenThrowIllegalStateException2() {
        // Arrange
        when(userRepositoryPort.findById(Mockito.<UserId>any())).thenThrow(new IllegalStateException("foo"));

        // Act and Assert
        assertThrows(IllegalStateException.class,
                () -> userService.findUserById(null)
        );
        verify(userRepositoryPort).findById(isNull());
    }
}
