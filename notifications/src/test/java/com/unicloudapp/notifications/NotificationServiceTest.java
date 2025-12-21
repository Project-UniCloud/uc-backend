package com.unicloudapp.notifications;

import com.unicloudapp.common.cloud.event.CloudUserCreatedEvent;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.UserLogin;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private UserQueryService userQueryService;
    private JavaMailSender mailSender;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        userQueryService = mock(UserQueryService.class);
        mailSender = mock(JavaMailSender.class);
        notificationService = new NotificationService(userQueryService, mailSender);
    }

    @Test
    @DisplayName("Should send email when CloudUserCreatedEvent is handled and user exists")
    void shouldSendEmailWhenCloudUserCreatedEventHandledAndUserExists() throws Exception {
        // given
        UserLogin userLogin = UserLogin.of("testuser");
        CloudUserCreatedEvent event = new CloudUserCreatedEvent(userLogin);
        UserDetails userDetails = UserDetails.builder()
                .login(userLogin)
                .email(Email.of("test@example.com"))
                .build();

        when(userQueryService.getUserDetailsByUsername(userLogin)).thenReturn(Optional.of(userDetails));
        
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        notificationService.handle(event);

        // then
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();
        
        assertThat(sentMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString()).isEqualTo("test@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Your access to cloud resources has been granted");
    }

    @Test
    @DisplayName("Should not send email when CloudUserCreatedEvent is handled and user does not exist")
    void shouldNotSendEmailWhenCloudUserCreatedEventHandledAndUserDoesNotExist() {
        // given
        UserLogin userLogin = UserLogin.of("nonexistent");
        CloudUserCreatedEvent event = new CloudUserCreatedEvent(userLogin);

        when(userQueryService.getUserDetailsByUsername(userLogin)).thenReturn(Optional.empty());

        // when
        notificationService.handle(event);

        // then
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when mail sending fails")
    void shouldThrowRuntimeExceptionWhenMailSendingFails() {
        // given
        UserLogin userLogin = UserLogin.of("testuser");
        CloudUserCreatedEvent event = new CloudUserCreatedEvent(userLogin);
        UserDetails userDetails = UserDetails.builder()
                .login(userLogin)
                .email(Email.of("test@example.com"))
                .build();

        when(userQueryService.getUserDetailsByUsername(userLogin)).thenReturn(Optional.of(userDetails));

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(MimeMessage.class));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> notificationService.handle(event));
    }
}
