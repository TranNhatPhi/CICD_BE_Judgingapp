package com.project.judging.Services;

import com.project.judging.DTOs.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceNew {
    private final Logger logger = LoggerFactory.getLogger(NotificationServiceNew.class);
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationServiceNew(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String message) {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        // Log the message before sending it
        logger.info("Sending notification with content: {}", message);
        messagingTemplate.convertAndSend("/notifications", notificationMessage);
    }

    public void sendMarkingRound1Notification(String message) {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        logger.info("Sending marking-round1 notification with content: {}", notificationMessage.getContent());
        messagingTemplate.convertAndSend("/topic/judge-marks", notificationMessage);
    }

}
