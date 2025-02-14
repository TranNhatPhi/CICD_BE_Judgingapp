package com.project.judging.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.judging.Config.Jwt.JwtUtils;
import com.project.judging.DTOs.AdminDTO.JudgeAssignedDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkRound2DTO;
import com.project.judging.DTOs.JudgeMarkingDTO.JudgeMarkedRound2Dto;
import com.project.judging.DTOs.ProjectDTO;
import com.project.judging.DTOs.ResponseDTO.APIConfigDTO;
import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.Mapper.Impl.ProjectMapper;
import com.project.judging.Entities.Project;
import com.project.judging.Services.Impl.ConfigService;
import com.project.judging.Services.Impl.ProjectServiceImpl;
import com.project.judging.Services.NotificationServiceNew;
import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project Management", description = "Project Management APIs")
public class ProjectController extends BaseController {

    private final ProjectServiceImpl projectService;
    private final ProjectMapper projectMapper;
    private final JwtUtils jwtUtils;
    private final ConfigService configService;
    private final NotificationServiceNew notificationService;

    public ProjectController(ProjectServiceImpl projectService, ProjectMapper projectMapper, JwtUtils jwtUtils, ConfigService configService, NotificationServiceNew notificationService) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.jwtUtils = jwtUtils;
        this.configService = configService;
        this.notificationService = notificationService;
    }

    // Both Roles
    @GetMapping("/showAll/{semesterId}")
    @Operation(summary = "Show All Projects", description = "Returns a list of all projects.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> getAllProjects(@PathVariable Integer semesterId) {
        List<ProjectDTO> projects = projectService.showAllProject(semesterId).stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
        return success("All projects", projects);
    }

    @GetMapping("/showAll")
    @Operation(summary = "Show All Projects", description = "Returns a list of all projects.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> getAllProjects(@RequestHeader("Authorization") String token) {
        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        List<ProjectDTO> projects = projectService.showAllProject(semesterId).stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
        return success("All projects", projects);
    }

    // Both Roles
    @GetMapping("/search")
    @Operation(summary = "Search Projects by Title", description = "Returns projects matching the given title.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> searchProjectByTitle(@Parameter(description = "Title of Project", required = true) @RequestParam(name="title") String title) {
        List<ProjectDTO> projects = projectService.searchProjectByTitle(title).stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
        return success("Projects found with title: " + title, projects);
    }

    // Admin Role
    @PostMapping("/create/{semesterId}")
    @Operation(summary = "Create New Project", description = "Creates a new project with the provided details.", tags = {"post", "admin"})
    public ResponseEntity<ResponseDTO<ProjectDTO>> createProject(@Parameter(description = "Project DTO", required = true) @RequestBody ProjectDTO projectDto, @PathVariable Integer semesterId) {
        Project project = projectMapper.toEntity(projectDto);
        Project projectCreated = projectService.addProject(project, semesterId);
        ProjectDTO projectDTO = this.projectMapper.toDto(projectCreated);
        return success("Project created successfully", projectDTO);
    }

    @PostMapping("/createProjects/{semesterId}")
    @Operation(summary = "Create New Projects", description = "Creates multiple projects with the provided details.", tags = {"post", "admin"})
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> createProjects(@RequestBody List<ProjectDTO> projectDTOs, @PathVariable Integer semesterId) {
        List<Project> projects = projectDTOs.stream().map(projectMapper::toEntity).collect(Collectors.toList());
        List<Project> createdProjects = projectService.addProjects(projects, semesterId);
        List<ProjectDTO> createdProjectDTOs = createdProjects.stream().map(projectMapper::toDto).collect(Collectors.toList());
        return success("Projects created successfully", createdProjectDTOs);
    }

    // Admin Role
    @PutMapping("/update/{projectId}")
    @Operation(summary = "Update Project", description = "Updates an existing project based on the provided ID.", tags = {"put", "admin"})
    public ResponseEntity<ResponseDTO<ProjectDTO>> updateProject(@PathVariable(name="projectId") Integer projectId, @RequestBody ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);
        Project projectCreated = projectService.editProject(projectId, project);
        ProjectDTO projectDto = this.projectMapper.toDto(projectCreated);
        return success("Project updated with id: " + projectDTO.getId(), projectDto);
    }

    // Admin Role
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Project", description = "Deletes a project based on the provided ID.", tags = {"delete", "admin"})
    public ResponseEntity<ResponseDTO<Void>> deleteProject(@Parameter(description = "ID of Project to dDelete", required = true) @PathVariable Integer id) {
        projectService.deleteProject(id);
        return success("Project deleted successfully", null);
    }

    @GetMapping("/assignedList/{semesterId}")
    @Operation(summary = "Get Project by ID", description = "Retrieves a project based on the provided ID.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<JudgeAssignedDTO>>> showAssignedJudges(@PathVariable Integer semesterId) {
        List<JudgeAssignedDTO> judges = projectService.showJudgesAssignedInProject(semesterId);
        return success("All judges assigned to projects", judges);
    }

    // Modify this one***
    @GetMapping("/round2List")
    public ResponseEntity<List<JudgeMarkedRound2Dto>> getJudgeMarkedRound2Dtos(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "judgeId") Integer judgeId) {
        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        List<JudgeMarkedRound2Dto> judgeMarkedRound2Dtos = projectService.judgeMarkedRound2Dtos(semesterId, judgeId);
        return ResponseEntity.ok(judgeMarkedRound2Dtos);
    }

    @GetMapping("/round2List/{semesterId}")
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> projectsInRound2(@PathVariable Integer semesterId) {
        List<Project> projects = projectService.projectsInRound2(semesterId);
        List <ProjectDTO> projectDTOs = projects.stream()
                .map(projectMapper::toDto)
                .toList();
        return success("Projects in Round 2", projectDTOs);
    }

    // View list of projects with Judges and Marks
    @GetMapping("/judge-marks/{semesterId}")
    public ResponseEntity<List<JudgeMarkDTO>> getProjectListWithJudgeMarks(@PathVariable Integer semesterId) {
        List<JudgeMarkDTO> judgeMarkDTOList = projectService.ProjectListWithJudgeMarks(semesterId);
        return ResponseEntity.ok(judgeMarkDTOList);
    }

    // View list of projects with Judges and Marks for Round 2
    @GetMapping("/judge-marks-round2/{semesterId}")
    public ResponseEntity<List<JudgeMarkRound2DTO>> getProjectListWithJudgeMarksRound2(@PathVariable Integer semesterId) {
        List<JudgeMarkRound2DTO> judgeMarkDTOList = projectService.ProjectListWithJudgeMarksRound2(semesterId);
        return ResponseEntity.ok(judgeMarkDTOList);
    }

    @PutMapping("/resetRank/{semesterId}")
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> resetRank(@PathVariable Integer semesterId) {
        List<Project> project = projectService.resetRank(semesterId);
        List<ProjectDTO> projectDTOs = project.stream()
                .filter(Project::isRound2Closed)
                .map(projectMapper::toDto)
                .toList();
        return success("Rank reset successfully", projectDTOs);
    }

    @PostMapping("/chooseRank/{semesterId}")
public ResponseEntity<ResponseDTO<List<ProjectDTO>>> chooseRank(@PathVariable Integer semesterId, @RequestBody List<Integer> projectIds) throws JsonProcessingException {
    List<Project> rankedProjects = projectService.chooseProjectToBeRanked(projectIds, semesterId);
    List<ProjectDTO> rankedProjectDTOs = rankedProjects.stream()
            .sorted((p1, p2) -> p2.getRank().compareTo(p1.getRank()))
            .map(projectMapper::toDto)
            .collect(Collectors.toList());
    Collections.reverse(rankedProjectDTOs);
    APIConfigDTO apiConfigDTO = configService.getAPIConfig(semesterId);
    ObjectMapper objectMapper = new ObjectMapper();
    String message = objectMapper.writeValueAsString(apiConfigDTO);
    notificationService.sendNotification(message);
    return success("Projects ranked successfully", rankedProjectDTOs);
}

    @PutMapping("/resetRound2/{semesterId}")
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> resetRound2(@PathVariable Integer semesterId) {
        List<Project> project = projectService.resetRound2(semesterId);
        List<ProjectDTO> projectDTOs = project.stream()
                .filter(Project::isRound1Closed)
                .map(projectMapper::toDto)
                .toList();
        return success("Round 2 reset successfully", projectDTOs);
    }

    @PostMapping("/import/{semesterId}")
    public ResponseEntity<String> importProjects(@RequestParam("file") MultipartFile file, @PathVariable Integer semesterId) {
        try {
            projectService.importProjectsFromExcel(file, semesterId);
            return ResponseEntity.ok("Projects imported successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error importing projects: " + e.getMessage());
        }
    }

    @GetMapping("/export/{semesterId}")
    public void exportProjectsWithRankInOrder(HttpServletResponse httpResponse, @PathVariable Integer semesterId) {
        try {
            projectService.exportProjectWithRankInOrder(httpResponse, semesterId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
