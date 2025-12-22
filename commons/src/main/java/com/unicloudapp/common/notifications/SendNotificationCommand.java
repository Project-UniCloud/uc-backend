package com.unicloudapp.common.notifications;

import lombok.Builder;

@Builder
public record SendNotificationCommand(String to, String subject, String text, NotificationType type) {}
