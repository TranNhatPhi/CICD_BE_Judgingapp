package com.project.judging.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.judging.Config.Jwt.JwtUtils;
import com.project.judging.DTOs.AdminConfigDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.MarkingStatusRoundDTO;
import com.project.judging.DTOs.JudgeMarkingDTO.MarkingRound1Dto;
import com.project.judging.DTOs.JudgeMarkingDTO.MarkingRound2Dto;
import com.project.judging.DTOs.JudgeMarkingDTO.MarkingTotalDtoReq;
import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.Entities.MarkingRound;
import com.project.judging.Mapper.Impl.MarkingRoundMapper;
import com.project.judging.Services.Impl.MarkingServiceImpl;

import java.util.ArrayList;
import java.util.List;

import com.project.judging.Services.Impl.NotificationService;
import com.project.judging.Services.NotificationServiceNew;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marking")
@Tag(name = "Marking Management", description = "Marking Management APIs")
public class MarkingController extends BaseController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private NotificationServiceNew notificationService;

    private final MarkingServiceImpl markingService;
    private final MarkingRoundMapper markingRoundMapper;

    Logger logger = org.slf4j.LoggerFactory.getLogger(MarkingController.class);

    public MarkingController(MarkingServiceImpl markingService,
                             MarkingRoundMapper markingRoundMapper) {
        this.markingService = markingService;
        this.markingRoundMapper = markingRoundMapper;
    }

    @PutMapping("/markRound1")
    public ResponseEntity<ResponseDTO<List<MarkingRound1Dto>>> markingRound1(
            @RequestHeader("Authorization") String token,
            @RequestBody List<MarkingRound1Dto> markingRound1Dtos) throws JsonProcessingException {

        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        List<MarkingRound1Dto> resultDtos = new ArrayList<>();

        for (MarkingRound1Dto dto: markingRound1Dtos) {
            List<MarkingRound> processedMarkingRounds = markingService.markProjectRound1(
                    semesterId,
                    dto.getJudgeId(),
                    dto.getProjectId(),
                    dto.getCriteriaMarks(),
                    dto.getComment());

            for (MarkingRound processing: processedMarkingRounds) {
                resultDtos.add(markingRoundMapper.round1Dto(processing));
            }
        }

        AdminConfigDTO adminCongigDTO = new AdminConfigDTO();
        adminCongigDTO.setRound1(true);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(adminCongigDTO);

        notificationService.sendNotification(message);
        logger.info("Sending marking-round1 notification with content: {}", message);
        // Check if the message is sent
        if (message != null) {
            logger.info("Message sent successfully");
        } else {
            logger.error("Message not sent");
        }

        return success("Marking Round 1 completed successfully", resultDtos);
    }


    @PutMapping("/markRound2")
    public ResponseEntity<ResponseDTO<List<MarkingRound2Dto>>> markingRound2(@RequestHeader("Authorization") String token, @RequestBody List<MarkingRound2Dto> markingRound2Dtos) throws JsonProcessingException {
        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        List<MarkingRound2Dto> resultDtos = new ArrayList<>();
        for (MarkingRound2Dto dto: markingRound2Dtos) {
            List<MarkingRound> processedMarkingRounds = markingService.markingRound2(semesterId,
                    dto.getJudgeId(),
                    dto.getProjectMarks(),
                    dto.getComment());
            for (MarkingRound processing: processedMarkingRounds) {
                resultDtos.add(markingRoundMapper.round2Dto(processing));
            }
        }
        AdminConfigDTO adminCongigDTO = new AdminConfigDTO();
        adminCongigDTO.setRound1(true);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(adminCongigDTO);
        notificationService.sendNotification(message);
        return success("Marking Round 2 completed successfully", resultDtos);
    }

    // Both Roles (Judges can see the projects assigned with mark to them)
    @GetMapping("/assigned/{judgeId}")
    @Operation(summary = "Get Assigned Projects with Total Marks for a Judge", description = "Returns a list of projects assigned to a judge with their total marks.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<MarkingTotalDtoReq>>> getAssignedProjects(@RequestHeader("Authorization") String token,
                                                                                     @PathVariable Integer judgeId) {
        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        List<MarkingTotalDtoReq> projects = markingService.getProjectsAssignedToJudgeWithTotalMarks(judgeId, semesterId);
        return success("Projects assigned to judge " + judgeId, projects);
    }

    @GetMapping("/markingRound1Status/{semesterId}")
    @Operation(summary = "Get Marking Round 1 Status", description = "Returns the status of marking round 1.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<MarkingStatusRoundDTO>>> markingRound1Status(@PathVariable Integer semesterId) {
        List<MarkingStatusRoundDTO> resultDtos = markingService.markingRound1Status(semesterId);
        return success("Marking Round 1 status", resultDtos);
    }

    @GetMapping("/markingRound2Status/{semesterId}")
    @Operation(summary = "Get Marking Round 2 Status", description = "Returns the status of marking round 2.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<MarkingStatusRoundDTO>>> markingRound2Status(@PathVariable Integer semesterId) {
        List<MarkingStatusRoundDTO> resultDtos = markingService.markingRound2Status(semesterId);
        return success("Marking Round 2 status", resultDtos);
    }

}