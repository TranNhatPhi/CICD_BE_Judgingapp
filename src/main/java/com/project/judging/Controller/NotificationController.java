package com.project.judging.Controller;

import com.project.judging.Services.NotificationServiceNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationServiceNew notificationService;

    @Autowired
    public NotificationController(NotificationServiceNew notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notify")
    public void sendNotification(@RequestParam String message) {

        notificationService.sendNotification(message);
    }
}

