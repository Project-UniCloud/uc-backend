package com.unicloudapp.user.application;

import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
