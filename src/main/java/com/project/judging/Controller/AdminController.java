package com.project.judging.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkingDTO;
import com.project.judging.DTOs.ProjectDTO;
import com.project.judging.DTOs.ResponseDTO.APIConfigDTO;
import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.Entities.Project;
import com.project.judging.Mapper.Impl.ProjectMapper;
import com.project.judging.Services.Impl.ConfigService;
import com.project.judging.Services.JudgeService;
import com.project.judging.Services.NotificationServiceNew;
import com.project.judging.Services.ProjectService;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "Admin Management APIs")
@CrossOrigin(origins = "*")
public class AdminController extends BaseController{

    private final ProjectService projectService;
    private final JudgeService judgeService;
    private final ProjectMapper projectMapper;
    private final ConfigService configService;
    private final NotificationServiceNew notificationService;

    public AdminController(ProjectService projectService, JudgeService judgeService, ProjectMapper projectMapper, ConfigService configService, NotificationServiceNew notificationService) {
        this.projectService = projectService;
        this.judgeService = judgeService;
        this.projectMapper = projectMapper;
        this.configService = configService;
        this.notificationService = notificationService;
    }

    // Admin Role
    @GetMapping("/config/{semesterId}")
    @Operation(summary = "Get API Config", description = "Get API Config", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<APIConfigDTO>> getAPIConfig(@PathVariable Integer semesterId) throws JsonProcessingException {
        APIConfigDTO apiConfigDTO = configService.getAPIConfig(semesterId);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(apiConfigDTO);
        notificationService.sendNotification(message);
        return success("Get API Config", configService.getAPIConfig(semesterId));
    }

    // Admin Role
    @GetMapping("/projects/{semesterId}/{projectId}")
    @Operation(summary = "Get group mark by Project ID", description = "Get project by ID", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<JudgeMarkingDTO>>> getJudgeMarkGroupByProjectId(@PathVariable Integer projectId, @PathVariable Integer semesterId) {
        projectService.getJudgeMarkGroupByProjectId(projectId, semesterId);
        return success("Get group mark by Project ID" + projectId, null);
    }

    // Admin Role
    @GetMapping("/highestRound1/{semesterId}")
    @Operation(summary = "Get highest projects' marks in Round 1", description = "Retrieves top projects from Round 1 based on their marks in descending order.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> getHighestProjectsInRound1(@PathVariable Integer semesterId) {
        List<Project> projects = projectService.getTopProjectsFromRound1InDescendingOrder(semesterId);
        return success("All Project are gone straight in Round 2", projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList()));
    }

    // Admin Role
    @GetMapping("/highestRound2/{semesterId}")
    @Operation(summary = "Get highest projects' marks in Round 2", description = "Retrieves top projects from Round 2 based on their marks in descending order.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> getHighestProjectsInRound2(@PathVariable Integer semesterId) {
        List<Project> projects = projectService.getTopProjectsFromRound2InDescendingOrder(semesterId);
        return success("Project In round 2 in descending order",projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList()));
    }
}
