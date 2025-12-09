package com.unicloudapp.user.application;

import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserFactory userFactory;

    // We cannot use @InjectMocks because UserService has package-private visibility,
    // but tests are in the same package so we can construct it directly in @BeforeEach.
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userFactory);
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
    void importStudents_allNew_savesAll_andReturnsAllIds() {
        // given
        var s1 = StudentBasicData.builder().firstName("John").lastName("Doe").email("j@d.com").login("john").build();
        var s2 = StudentBasicData.builder().firstName("Anna").lastName("Smith").email("a@s.com").login("anna").build();
        List<StudentBasicData> input = List.of(s1, s2);

        // Prepare two mocked users created by factory
        User u1 = mock(User.class);
        User u2 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userFactory.create(any(), eq(UserLogin.of("john")), any(), any(), any(), any())).thenReturn(u1);
        when(userFactory.create(any(), eq(UserLogin.of("anna")), any(), any(), any(), any())).thenReturn(u2);

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
        var s1 = StudentBasicData.builder().firstName("John").lastName("Doe").email("john@example.com").login("john").build();
        var s2 = StudentBasicData.builder().firstName("Anna").lastName("Smith").email("anna@example.com").login("anna").build();
        var s3 = StudentBasicData.builder().firstName("Mike").lastName("Miles").email("mike@example.com").login("mike").build();
        List<StudentBasicData> input = List.of(s1, s2, s3);

        User u1 = mock(User.class);
        User u2 = mock(User.class);
        User u3 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        when(userFactory.create(any(), eq(UserLogin.of("john")), any(), any(), any(), any())).thenReturn(u1);
        when(userFactory.create(any(), eq(UserLogin.of("anna")), any(), any(), any(), any())).thenReturn(u2);
        when(userFactory.create(any(), eq(UserLogin.of("mike")), any(), any(), any(), any())).thenReturn(u3);

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
        var s1 = StudentBasicData.builder().firstName("John").lastName("Doe").email("john@example.com").login("john").build();
        var s2 = StudentBasicData.builder().firstName("Anna").lastName("Smith").email("anna@example.com").login("anna").build();
        List<StudentBasicData> input = List.of(s1, s2);

        User u1 = mock(User.class);
        User u2 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userFactory.create(any(), eq(UserLogin.of("john")), any(), any(), any(), any())).thenReturn(u1);
        when(userFactory.create(any(), eq(UserLogin.of("anna")), any(), any(), any(), any())).thenReturn(u2);

        when(u1.getUserId()).thenReturn(UserId.of(id1));
        when(u2.getUserId()).thenReturn(UserId.of(id2));
        when(u1.getUserLogin()).thenReturn(UserLogin.of("john"));
        when(u2.getUserLogin()).thenReturn(UserLogin.of("anna"));

        when(userRepository.existsByLogin("john")).thenReturn(true);
        when(userRepository.existsByLogin("anna")).thenReturn(true);

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
}
