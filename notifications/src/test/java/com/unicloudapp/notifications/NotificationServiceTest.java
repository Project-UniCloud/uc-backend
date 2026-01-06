package com.unicloudapp.notifications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.unicloudapp.common.cloud.event.CloudBudgetThresholdExceededEvent;
import com.unicloudapp.common.cloud.event.CloudUserCreatedEvent;
import com.unicloudapp.common.group.GroupDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

class NotificationServiceTest {

    private UserQueryService userQueryService;
    private JavaMailSender mailSender;
    private GroupQueryService groupQueryService;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        userQueryService = mock(UserQueryService.class);
        mailSender = mock(JavaMailSender.class);
        groupQueryService = mock(GroupQueryService.class);
        notificationService = new NotificationService(userQueryService, mailSender, groupQueryService);
    }

    @Test
    @DisplayName("Should send email when CloudUserCreatedEvent is handled and user exists")
    void shouldSendEmailWhenCloudUserCreatedEventHandledAndUserExists() throws Exception {
        // given
        UserLogin userLogin = UserLogin.of("testuser");
        var groupUniqueName = GroupUniqueName.fromString("AI 2024L");
        CloudUserCreatedEvent event = new CloudUserCreatedEvent(userLogin, groupUniqueName);
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

        assertThat(sentMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString())
                .isEqualTo("test@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Your access to cloud resources has been granted");
    }

    @Test
    @DisplayName("Should not send email when CloudUserCreatedEvent is handled and user does not exist")
    void shouldNotSendEmailWhenCloudUserCreatedEventHandledAndUserDoesNotExist() {
        // given
        UserLogin userLogin = UserLogin.of("nonexistent");
        var groupUniqueName = GroupUniqueName.fromString("AI 2024L");
        CloudUserCreatedEvent event = new CloudUserCreatedEvent(userLogin, groupUniqueName);

        when(userQueryService.getUserDetailsByUsername(userLogin)).thenReturn(Optional.empty());

        // when
        notificationService.handle(event);

        // then
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should log error but not throw exception when mail sending fails")
    void shouldLogErrorButNotThrowExceptionWhenMailSendingFails() {
        // given
        UserLogin userLogin = UserLogin.of("testuser");
        var groupUniqueName = GroupUniqueName.fromString("AI 2024L");
        CloudUserCreatedEvent event = new CloudUserCreatedEvent(userLogin, groupUniqueName);
        UserDetails userDetails = UserDetails.builder()
                .login(userLogin)
                .email(Email.of("test@example.com"))
                .build();

        when(userQueryService.getUserDetailsByUsername(userLogin)).thenReturn(Optional.of(userDetails));

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(MimeMessage.class));

        // when & then
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> notificationService.handle(event));
    }

    @Test
    @DisplayName("Should send email to admins when CloudBudgetThresholdExceededEvent is handled")
    void shouldSendEmailToAdminsWhenCloudBudgetThresholdExceededEventHandled() {
        // given
        CloudResourceAccessId cloudResourceAccessId = CloudResourceAccessId.of(UUID.randomUUID());
        CloudBudgetThresholdExceededEvent event = CloudBudgetThresholdExceededEvent.builder()
                .cloudResourceAccessId(cloudResourceAccessId)
                .notificationLevel(80)
                .limit(UsedLimit.of(new BigDecimal("85.00")))
                .costLimit(CostLimit.of(new BigDecimal("100.00")))
                .occurredAt(Instant.now())
                .build();

        UserDetails admin = UserDetails.builder()
                .login(UserLogin.of("admin"))
                .email(Email.of("admin@example.com"))
                .build();

        when(userQueryService.getAdmins()).thenReturn(List.of(admin));

        UserId lecturerId = UserId.of(UUID.randomUUID());
        when(groupQueryService.getGroupByCloudResourceAccess(cloudResourceAccessId))
                .thenReturn(new GroupDto(Set.of(lecturerId)));

        UserLogin lecturerLogin = UserLogin.of("lecturer");
        Email lecturerEmail = Email.of("lecturer@example.com");
        when(userQueryService.getUserLoginsAndEmailsByIds(Set.of(lecturerId)))
                .thenReturn(List.of(Map.entry(lecturerLogin, lecturerEmail)));

        MimeMessage adminMimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        MimeMessage lecturerMimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(adminMimeMessage).thenReturn(lecturerMimeMessage);

        // when
        notificationService.handle(event);

        // then
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(2)).send(captor.capture());
        List<MimeMessage> sentMessages = captor.getAllValues();

        assertThat(sentMessages).hasSize(2);
        assertThat(sentMessages.stream().anyMatch(msg -> {
                    try {
                        return msg.getRecipients(MimeMessage.RecipientType.TO)[0]
                                .toString()
                                .equals("admin@example.com");
                    } catch (Exception e) {
                        return false;
                    }
                }))
                .isTrue();
        assertThat(sentMessages.stream().anyMatch(msg -> {
                    try {
                        return msg.getRecipients(MimeMessage.RecipientType.TO)[0]
                                .toString()
                                .equals("lecturer@example.com");
                    } catch (Exception e) {
                        return false;
                    }
                }))
                .isTrue();
    }
}
