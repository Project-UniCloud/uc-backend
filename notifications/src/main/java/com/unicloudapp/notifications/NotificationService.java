package com.unicloudapp.notifications;

import com.unicloudapp.common.notifications.NotificationsCommandService;
import com.unicloudapp.common.notifications.SendNotificationCommand;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class NotificationService implements NotificationsCommandService {

    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(SendNotificationCommand sendNotificationCommand) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(sendNotificationCommand.to());
            helper.setSubject(sendNotificationCommand.subject());
            helper.setText(sendNotificationCommand.text(), true);
            mailSender.send(mimeMessage);
            System.out.println("📨 Wysłano e-mail HTML do " + sendNotificationCommand.to());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
