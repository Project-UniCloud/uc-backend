package com.unicloudapp.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.unicloudapp.common.auth.AdminProperties;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.*;
import com.unicloudapp.user.application.command.UpdateUserCommand;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private AdminProperties adminProperties;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userFactory, adminProperties);
    }

    @Test
    @DisplayName("getUserDetailsByUsername should return user details when user exists")
    void getUserDetailsByUsername_returnsDetails_whenUserExists() {
        // given
        UserLogin login = UserLogin.of("jdoe");
        User user = mock(User.class);
        UserId userId = UserId.of(UUID.randomUUID());
        FirstName firstName = FirstName.of("John");
        LastName lastName = LastName.of("Doe");
        Email email = Email.of("john@doe.com");
        UserRole role = UserRole.of(UserRole.Type.STUDENT);

        when(user.getUserId()).thenReturn(userId);
        when(user.getUserLogin()).thenReturn(login);
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getLastName()).thenReturn(lastName);
        when(user.getEmail()).thenReturn(email);
        when(user.getUserRole()).thenReturn(role);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        // when
        Optional<com.unicloudapp.common.user.UserDetails> result = userService.getUserDetailsByUsername(login);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().userId()).isEqualTo(userId);
        assertThat(result.get().login()).isEqualTo(login);
        assertThat(result.get().firstName()).isEqualTo(firstName);
        assertThat(result.get().lastName()).isEqualTo(lastName);
        assertThat(result.get().email()).isEqualTo(email);
        assertThat(result.get().roles()).isEqualTo(role);
    }

    @Test
    @DisplayName("getUserDetailsByUsername should return empty when user does not exist")
    void getUserDetailsByUsername_returnsEmpty_whenUserDoesNotExist() {
        // given
        UserLogin login = UserLogin.of("nonexistent");
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // when
        Optional<com.unicloudapp.common.user.UserDetails> result = userService.getUserDetailsByUsername(login);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByLogin should return true when repository reports existing login")
    void existsByLogin_returnsTrue_whenRepositoryReturnsTrue() {
        // given
        String login = "jdoe";
        when(userRepository.existsByLogin(login)).thenReturn(true);

        // when
        boolean exists = userService.existsByLogin(login);

        // then
        assertThat(exists).isTrue();
        verify(userRepository, times(1)).existsByLogin(login);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("existsByLogin should return false when repository reports missing login")
    void existsByLogin_returnsFalse_whenRepositoryReturnsFalse() {
        // given
        String login = "nonexistent";
        when(userRepository.existsByLogin(login)).thenReturn(false);

        // when
        boolean exists = userService.existsByLogin(login);

        // then
        assertThat(exists).isFalse();
        verify(userRepository, times(1)).existsByLogin(login);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("importStudents should save all new users and return all generated IDs")
    @SuppressWarnings("unchecked")
    void importStudents_allNew_savesAll_andReturnsAllIds() {
        // given
        var s1 = StudentBasicData.builder()
                .firstName("John")
                .lastName("Doe")
                .email("j@d.com")
                .login("john")
                .build();
        var s2 = StudentBasicData.builder()
                .firstName("Anna")
                .lastName("Smith")
                .email("a@s.com")
                .login("anna")
                .build();
        List<StudentBasicData> input = List.of(s1, s2);

        // Prepare two mocked users created by factory
        User u1 = mock(User.class);
        User u2 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userFactory.create(any(), eq(UserLogin.of("john")), any(), any(), any(), any()))
                .thenReturn(u1);
        when(userFactory.create(any(), eq(UserLogin.of("anna")), any(), any(), any(), any()))
                .thenReturn(u2);

        when(u1.getUserId()).thenReturn(UserId.of(id1));
        when(u2.getUserId()).thenReturn(UserId.of(id2));
        when(u1.getUserLogin()).thenReturn(UserLogin.of("john"));
        when(u2.getUserLogin()).thenReturn(UserLogin.of("anna"));

        // All are new -> repository.existsByLogin returns false
        when(userRepository.existsByLogin("john")).thenReturn(false);
        when(userRepository.existsByLogin("anna")).thenReturn(false);

        ArgumentCaptor<List<User>> toSaveCaptor = ArgumentCaptor.forClass(List.class);
        when(userRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<UserId> ids = userService.importStudents(input);

        // then
        verify(userRepository).saveAll(toSaveCaptor.capture());
        List<User> saved = toSaveCaptor.getValue();
        assertThat(saved).containsExactlyInAnyOrder(u1, u2);
        assertThat(ids).containsExactlyInAnyOrder(UserId.of(id1), UserId.of(id2));
    }

    @Test
    @DisplayName("importStudents should save only non-existing users but return all IDs")
    void importStudents_mixed_savesOnlyNew_andReturnsAllIds() {
        // given
        var s1 = StudentBasicData.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .login("john")
                .build();
        var s2 = StudentBasicData.builder()
                .firstName("Anna")
                .lastName("Smith")
                .email("anna@example.com")
                .login("anna")
                .build();
        var s3 = StudentBasicData.builder()
                .firstName("Mike")
                .lastName("Miles")
                .email("mike@example.com")
                .login("mike")
                .build();
        List<StudentBasicData> input = List.of(s1, s2, s3);

        User u1 = mock(User.class);
        User u2 = mock(User.class);
        User u3 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        when(userFactory.create(any(), eq(UserLogin.of("john")), any(), any(), any(), any()))
                .thenReturn(u1);
        when(userFactory.create(any(), eq(UserLogin.of("anna")), any(), any(), any(), any()))
                .thenReturn(u2);
        when(userFactory.create(any(), eq(UserLogin.of("mike")), any(), any(), any(), any()))
                .thenReturn(u3);

        when(u1.getUserId()).thenReturn(UserId.of(id1));
        when(u2.getUserId()).thenReturn(UserId.of(id2));
        when(u3.getUserId()).thenReturn(UserId.of(id3));
        when(u1.getUserLogin()).thenReturn(UserLogin.of("john"));
        when(u2.getUserLogin()).thenReturn(UserLogin.of("anna"));
        when(u3.getUserLogin()).thenReturn(UserLogin.of("mike"));

        // john exists, anna new, mike exists
        when(userRepository.existsByLogin("john")).thenReturn(true);
        when(userRepository.existsByLogin("anna")).thenReturn(false);
        when(userRepository.existsByLogin("mike")).thenReturn(true);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> toSaveCaptor = ArgumentCaptor.forClass(List.class);
        when(userRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<UserId> ids = userService.importStudents(input);

        // then
        verify(userRepository).saveAll(toSaveCaptor.capture());
        List<User> saved = toSaveCaptor.getValue();
        assertThat(saved).containsExactly(u2);
        assertThat(ids).containsExactlyInAnyOrder(UserId.of(id1), UserId.of(id2), UserId.of(id3));
    }

    @Test
    @DisplayName("importStudents should not save any when all users already exist, but still return all IDs")
    void importStudents_allExisting_savesNone_andReturnsAllIds() {
        // given
        var s1 = StudentBasicData.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .login("john")
                .build();
        var s2 = StudentBasicData.builder()
                .firstName("Anna")
                .lastName("Smith")
                .email("anna@example.com")
                .login("anna")
                .build();
        List<StudentBasicData> input = List.of(s1, s2);

        User u1 = mock(User.class);
        User u2 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userFactory.create(any(), eq(UserLogin.of("john")), any(), any(), any(), any()))
                .thenReturn(u1);
        when(userFactory.create(any(), eq(UserLogin.of("anna")), any(), any(), any(), any()))
                .thenReturn(u2);

        when(u1.getUserId()).thenReturn(UserId.of(id1));
        when(u2.getUserId()).thenReturn(UserId.of(id2));
        when(u1.getUserLogin()).thenReturn(UserLogin.of("john"));
        when(u2.getUserLogin()).thenReturn(UserLogin.of("anna"));

        when(userRepository.existsByLogin("john")).thenReturn(true);
        when(userRepository.existsByLogin("anna")).thenReturn(true);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> toSaveCaptor = ArgumentCaptor.forClass(List.class);
        when(userRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<UserId> ids = userService.importStudents(input);

        // then
        verify(userRepository).saveAll(toSaveCaptor.capture());
        List<User> saved = toSaveCaptor.getValue();
        assertThat(saved).isEmpty();
        assertThat(ids).containsExactlyInAnyOrder(UserId.of(id1), UserId.of(id2));
    }

    @Test
    @DisplayName("getAdmins should return list of admins when they exist")
    void getAdmins_returnsAdmins_whenTheyExist() {
        // given
        User admin1 = mock(User.class);
        User admin2 = mock(User.class);

        UserId id1 = UserId.of(UUID.randomUUID());
        UserLogin login1 = UserLogin.of("admin1");
        FirstName fn1 = FirstName.of("Admin");
        LastName ln1 = LastName.of("One");
        Email email1 = Email.of("admin1@example.com");
        UserRole role = UserRole.of(UserRole.Type.ADMIN);

        when(admin1.getUserId()).thenReturn(id1);
        when(admin1.getUserLogin()).thenReturn(login1);
        when(admin1.getFirstName()).thenReturn(fn1);
        when(admin1.getLastName()).thenReturn(ln1);
        when(admin1.getEmail()).thenReturn(email1);
        when(admin1.getUserRole()).thenReturn(role);

        UserId id2 = UserId.of(UUID.randomUUID());
        UserLogin login2 = UserLogin.of("admin2");
        FirstName fn2 = FirstName.of("Admin");
        LastName ln2 = LastName.of("Two");
        Email email2 = Email.of("admin2@example.com");

        when(admin2.getUserId()).thenReturn(id2);
        when(admin2.getUserLogin()).thenReturn(login2);
        when(admin2.getFirstName()).thenReturn(fn2);
        when(admin2.getLastName()).thenReturn(ln2);
        when(admin2.getEmail()).thenReturn(email2);
        when(admin2.getUserRole()).thenReturn(role);

        when(userRepository.findAllByRole(role)).thenReturn(List.of(admin1, admin2));

        // when
        List<com.unicloudapp.common.user.UserDetails> result = userService.getAdmins();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId()).isEqualTo(id1);
        assertThat(result.get(1).userId()).isEqualTo(id2);
        verify(userRepository).findAllByRole(role);
    }

    @Test
    @DisplayName("getAdmins should return empty list when no admins exist")
    void getAdmins_returnsEmptyList_whenNoAdminsExist() {
        // given
        UserRole role = UserRole.of(UserRole.Type.ADMIN);
        when(userRepository.findAllByRole(role)).thenReturn(List.of());

        // when
        List<com.unicloudapp.common.user.UserDetails> result = userService.getAdmins();

        // then
        assertThat(result).isEmpty();
        verify(userRepository).findAllByRole(role);
    }

    @Test
    @DisplayName("updateUser should update user details when user exists")
    void updateUser_updatesExistingUser_whenUserExists() {
        // given
        UserId userId = UserId.of(UUID.randomUUID());
        UpdateUserCommand command = UpdateUserCommand.builder()
                .userId(userId)
                .firstName(FirstName.of("Jane"))
                .lastName(LastName.of("Smith"))
                .email(Email.of("jane.smith@example.com"))
                .build();

        User existingUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // when
        userService.updateUser(command);

        // then
        verify(existingUser).setFirstName(command.firstName());
        verify(existingUser).setLastName(command.lastName());
        verify(existingUser).setEmail(command.email());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("updateUser should throw UserNotFoundException when user does not exist")
    void updateUser_throwsUserNotFoundException_whenUserDoesNotExist() {
        // given
        UserId userId = UserId.of(UUID.randomUUID());
        UpdateUserCommand command = UpdateUserCommand.builder()
                .userId(userId)
                .firstName(FirstName.of("Jane"))
                .lastName(LastName.of("Smith"))
                .email(Email.of("jane.smith@example.com"))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(
                com.unicloudapp.common.exception.user.UserNotFoundException.class,
                () -> userService.updateUser(command));
    }
}
