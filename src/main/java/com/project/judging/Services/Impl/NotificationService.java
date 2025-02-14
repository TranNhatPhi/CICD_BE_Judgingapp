package com.project.judging.Services.Impl;

import com.project.judging.DTOs.ResponseDTO.APIConfigDTO;
import com.project.judging.Entities.Semester;
import com.project.judging.Repositories.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Value("/specific")
    private String baseUrl;

    private final SimpMessagingTemplate messagingTemplate;
    private final ConfigService apiConfigService;
    private final SemesterRepository semesterRepository;

    public NotificationService(SimpMessagingTemplate messagingTemplate, ConfigService apiConfigService, SemesterRepository semesterRepository) {
        this.messagingTemplate = messagingTemplate;
        this.apiConfigService = apiConfigService;
        this.semesterRepository = semesterRepository;
    }

    public void sendMarkingRound1Notification() {
        Map<String, Object> message = new HashMap<>();
        message.put("shouldRefetchResultRound1", true);
        messagingTemplate.convertAndSend(baseUrl + "/markingRound1", message);
    }

    // Example: Method to send a message for marking round 2
    public void sendMarkingRound2Notification() {
        Map<String, Object> message = new HashMap<>();
        message.put("shouldRefetchResultRound2", true);
        messagingTemplate.convertAndSend(baseUrl + "/markingRound2", message);
    }

    public void sendingRoundClosedNotification(Integer semesterId) {
        APIConfigDTO apiConfigDTO = apiConfigService.getAPIConfig(semesterId);

        Map<String, Object> message = new HashMap<>();
        message.put("isRound1Closed", apiConfigDTO.isRound1Closed());
        message.put("isRound2Closed", apiConfigDTO.isRound2Closed());

        messagingTemplate.convertAndSend(baseUrl + "/roundClosed", message);
    }

    public void sendingEventNameNotification(Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId).orElse(null);

        Map<String, Object> message = new HashMap<>();
        message.put("eventName", semester.getEventName());
        message.put("description", semester.getDescription());
        messagingTemplate.convertAndSend(baseUrl + "/eventName", message);
    }
}

