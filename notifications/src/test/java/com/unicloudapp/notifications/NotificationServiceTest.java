package com.unicloudapp.notifications;

import com.unicloudapp.common.notifications.NotificationType;
import com.unicloudapp.common.notifications.SendNotificationCommand;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService service;

    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    private Session session;

    @BeforeEach
    void setUp() {
        session = Session.getInstance(new Properties());
    }

    @Test
    void sendNotification_sendsMailWithExpectedFields() throws Exception {
        // given
        MimeMessage mimeMessage = new MimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        SendNotificationCommand cmd = SendNotificationCommand.builder()
                .to("john.doe@example.com")
                .subject("Welcome")
                .text("<h1>Hello</h1><p>Welcome on board.</p>")
                .type(NotificationType.EMAIL)
                .build();

        // when
        service.sendNotification(cmd);

        // then
        verify(mailSender).send(messageCaptor.capture());
        MimeMessage sent = messageCaptor.getValue();

        // verify recipient
        Address[] recipients = sent.getAllRecipients();
        assertThat(recipients).isNotNull();
        assertThat(recipients).hasSize(1);
        assertThat(((InternetAddress) recipients[0]).getAddress()).isEqualTo("john.doe@example.com");

        // verify subject
        assertThat(sent.getSubject()).isEqualTo("Welcome");

        // verify that content contains our HTML body
        String flatContent = extractTextContent(sent);
        assertThat(flatContent).contains("Hello");
        assertThat(flatContent).contains("Welcome on board.");
    }

    @Test
    void sendNotification_wrapsMessagingExceptionIntoRuntime() {
        // given: a MimeMessage that throws MessagingException on subject set
        MimeMessage throwing = new ThrowingMimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(throwing);

        SendNotificationCommand cmd = SendNotificationCommand.builder()
                .to("john.doe@example.com")
                .subject("Any")
                .text("Body")
                .type(NotificationType.EMAIL)
                .build();

        // when / then
        assertThatThrownBy(() -> service.sendNotification(cmd))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(MessagingException.class);

        // and mailSender.send should not be called because building message failed
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    // Helpers
    private static String extractTextContent(MimeMessage message) throws MessagingException, IOException {
        Object content = message.getContent();
        switch (content) {
            case null -> {
                return "";
            }
            case String s -> {
                return s;
            }

            // Many Jakarta Mail implementations return MimeMultipart for HTML; do a best-effort extraction
            case jakarta.mail.Multipart multipart -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < multipart.getCount(); i++) {
                    var bodyPart = multipart.getBodyPart(i);
                    Object partContent = bodyPart.getContent();
                    if (partContent instanceof String s) {
                        sb.append(s);
                    } else if (partContent instanceof jakarta.mail.Multipart nested) {
                        // recurse one level
                        for (int j = 0; j < nested.getCount(); j++) {
                            Object nestedContent = nested.getBodyPart(j).getContent();
                            if (nestedContent instanceof String ns) {
                                sb.append(ns);
                            }
                        }
                    }
                }
                return sb.toString();
            }
            default -> {
            }
        }
        return content.toString();
    }

    // Test double MimeMessage that throws MessagingException when setting subject/recipients via helper
    static class ThrowingMimeMessage extends MimeMessage {
        public ThrowingMimeMessage(Session session) { super(session); }

        @Override
        public void setSubject(String subject, String charset) throws MessagingException {
            throw new MessagingException("boom on setSubject");
        }

        @Override
        public void setRecipients(jakarta.mail.Message.RecipientType type, Address[] addresses) throws MessagingException {
            throw new MessagingException("boom on setRecipients");
        }
    }
}
