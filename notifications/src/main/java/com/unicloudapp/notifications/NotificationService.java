package com.unicloudapp.notifications;

import com.unicloudapp.common.cloud.event.CloudBudgetThresholdExceededEvent;
import com.unicloudapp.common.cloud.event.CloudUserCreatedEvent;
import com.unicloudapp.common.group.GroupDto;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.notifications.NotificationType;
import com.unicloudapp.common.notifications.SendNotificationCommand;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.UserLogin;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserQueryService userQueryService;
    private final JavaMailSender mailSender;
    private final GroupQueryService groupQueryService;

    private static final String mail;
    private static final String budgetMail;

    static {
        try {
            mail = new String(
                    new ClassPathResource("mail.html").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            budgetMail = new String(
                    new ClassPathResource("budget_threshold_exceeded.html")
                            .getInputStream()
                            .readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to load email template", e);
            throw new RuntimeException(e);
        }
    }

    @Async
    @EventListener(CloudUserCreatedEvent.class)
    protected void handle(CloudUserCreatedEvent event) {
        UserLogin userLogin = event.userLogin();
        Optional<UserDetails> details = userQueryService.getUserDetailsByUsername(userLogin);
        details.ifPresent(userDetails -> {
            SendNotificationCommand sendNotificationCommand = SendNotificationCommand.builder()
                    .to(userDetails.email().getValue())
                    .subject("Your access to cloud resources has been granted")
                    .text(mail.replace(
                                    "{username}",
                                    userLogin.getValue() + "-"
                                            + event.groupUniqueName().getGroupNameWithoutSpaces())
                            .replace("{password}", userLogin.getValue() + "_password123$"))
                    .type(NotificationType.EMAIL)
                    .build();
            sendNotification(sendNotificationCommand);
        });
    }

    @Async
    @EventListener(CloudBudgetThresholdExceededEvent.class)
    protected void handle(CloudBudgetThresholdExceededEvent event) {
        if (event.notificationLevel() == null) {
            return;
        }
        List<UserDetails> admins = userQueryService.getAdmins();
        GroupDto groupOfCloudResourceAccess =
                groupQueryService.getGroupByCloudResourceAccess(event.cloudResourceAccessId());
        List<Map.Entry<UserLogin, Email>> lecturers =
                userQueryService.getUserLoginsAndEmailsByIds(groupOfCloudResourceAccess.lecturers());
        admins.forEach(admin -> {
            SendNotificationCommand sendNotificationCommand = SendNotificationCommand.builder()
                    .to(admin.email().getValue())
                    .subject("Cloud Budget Threshold Exceeded")
                    .text(budgetMail
                            .replace("{notificationLevel}", String.valueOf(event.notificationLevel()))
                            .replace(
                                    "{cloudResourceAccessId}",
                                    event.cloudResourceAccessId().getValue().toString())
                            .replace("{currentCost}", event.limit().getValue().toString())
                            .replace("{costLimit}", event.costLimit().getCost().toString())
                            .replace("{currency}", "USD")
                            .replace("{occurredAt}", event.occurredAt().toString()))
                    .type(NotificationType.EMAIL)
                    .build();
            sendNotification(sendNotificationCommand);
        });
        lecturers.forEach(lecturer -> {
            SendNotificationCommand sendNotificationCommand = SendNotificationCommand.builder()
                    .to(lecturer.getValue().getValue())
                    .subject("Cloud Budget Threshold Exceeded")
                    .text(budgetMail
                            .replace("{notificationLevel}", String.valueOf(event.notificationLevel()))
                            .replace(
                                    "{cloudResourceAccessId}",
                                    event.cloudResourceAccessId().getValue().toString())
                            .replace("{currentCost}", event.limit().getValue().toString())
                            .replace("{costLimit}", event.costLimit().getCost().toString())
                            .replace("{currency}", "USD")
                            .replace("{occurredAt}", event.occurredAt().toString()))
                    .type(NotificationType.EMAIL)
                    .build();
            sendNotification(sendNotificationCommand);
        });
    }

    private void sendNotification(SendNotificationCommand sendNotificationCommand) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(sendNotificationCommand.to());
            helper.setSubject(sendNotificationCommand.subject());
            helper.setText(sendNotificationCommand.text(), true);
            helper.setFrom("notifications@unicloud.projektstudencki.pl", "Unicloud");
            mailSender.send(mimeMessage);
            log.info("E-mail sent to {}", sendNotificationCommand.to());
        } catch (Exception e) {
            log.error("Error while sending email to {}", sendNotificationCommand.to(), e);
        }
    }
}
