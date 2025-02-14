package com.project.judging.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.judging.Config.Jwt.JwtUtils;
import com.project.judging.DTOs.AdminDTO.JudgeAccountDTO;
import com.project.judging.DTOs.AdminDTO.JudgeWithAssignedProjectsDTO;
import com.project.judging.DTOs.AdminDTO.JudgesListAndAssignedDTO;
import com.project.judging.DTOs.JudgeDTO;
import com.project.judging.DTOs.ProjectDTO;
import com.project.judging.DTOs.ResponseDTO.APIConfigDTO;
import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.Mapper.Impl.JudgeMapper;
import com.project.judging.Mapper.Impl.ProjectMapper;
import com.project.judging.Services.Impl.ConfigService;
import com.project.judging.Services.Impl.JudgeServiceImpl;
import com.project.judging.Entities.Judge;
import com.project.judging.Entities.Project;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.project.judging.Services.NotificationServiceNew;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Judge Management", description = "Judge Mangement APIs")
public class JudgeController extends BaseController {

    private final JudgeServiceImpl judgeService;
    private final JudgeMapper judgeMapper;
    private final ProjectMapper projectMapper;
    private final JwtUtils jwtUtils;
    private final ConfigService configService;
    private final NotificationServiceNew notificationService;

    public JudgeController(JudgeServiceImpl judgeService,
                           JudgeMapper judgeMapper,
                           ProjectMapper projectMapper, JwtUtils jwtUtils, ConfigService configService, NotificationServiceNew notificationService) {
        this.judgeService = judgeService;
        this.judgeMapper = judgeMapper;
        this.projectMapper = projectMapper;
        this.jwtUtils = jwtUtils;
        this.configService = configService;
        this.notificationService = notificationService;
    }

    @GetMapping("/config")
    @Operation(summary = "Get API Config", description = "Get API Config", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<APIConfigDTO>> getAPIConfig(@RequestHeader("Authorization") String token) {
        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        return success("Get API Config", configService.getAPIConfig(semesterId));
    }

    // Both Roles
    @GetMapping("/showAll/{semesterId}")
    @Operation(summary = "Show All Judges", description = "Returns a list of all judges.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<JudgeDTO>>> showAllAccount(@PathVariable Integer semesterId) {
        List<JudgeDTO> judges = this.judgeService.showAllAccount(semesterId).stream()
                .map(judgeMapper::toDto)
                .collect(Collectors.toList());
        return success("All judges", judges);
    }

    @PutMapping("/toRound2/{semesterId}")
    public ResponseEntity<ResponseDTO<List<ProjectDTO>>> projectToBeRound2(@PathVariable Integer semesterId, @RequestBody List<Integer> projectIds) {
        try {
            List<Project> updatedProjects = judgeService.projectTobeRound2(projectIds, semesterId);
            List<ProjectDTO> projectDTOList = updatedProjects.stream()
                    .map(projectMapper::toDto)
                    .toList();
            APIConfigDTO apiConfigDTO = configService.getAPIConfig(semesterId);
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(apiConfigDTO);
            notificationService.sendNotification(message);
            return success("Projects are now in Round 2", projectDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping ("/generate")
    @Operation(summary = "Generate Password", description = "Generates a random password.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<String>> generatePassword() {
        String password = this.judgeService.passwordGenerator();
        return success("Password generated successfully", password);
    }

    @PostMapping("/create/{semesterId}")
    @Operation(summary="User Register", description = "Register the user in the system", tags={"post"})
    public ResponseEntity<JudgeDTO> registerUser(@PathVariable Integer semesterId,
                                                 @RequestBody JudgeDTO judgeDTO, HttpServletResponse httpResponse) {
        Judge newJudge = this.judgeMapper.toEntity(judgeDTO);
        Judge savedJudge = this.judgeService.setInitWithExcelExports(newJudge, semesterId);
        JudgeDTO saveJudgeDTO = this.judgeMapper.toDto(savedJudge);
        return new ResponseEntity<>(saveJudgeDTO, HttpStatus.CREATED);
    }

    @GetMapping("/export/{semesterId}")
    @ResponseStatus(HttpStatus.OK)
    public void exportAccountsBySemester(@PathVariable Integer semesterId, HttpServletResponse httpResponse) {
        judgeService.exportAccountsBySemester(httpResponse, semesterId);
    }

    @GetMapping("/viewAccount/{semesterId}")
    @Operation(summary = "View Judges Account", description = "Returns a list of all judges account.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<JudgeAccountDTO>>> viewJudgesAccount(@PathVariable Integer semesterId) {
        List<JudgeAccountDTO> judgeAccountDTOList = this.judgeService.viewJudgesAccount(semesterId);
        return success("All judges account", judgeAccountDTOList);
    }

    @PutMapping("/changePassword")
    @Operation(summary = "Change Password", description = "Changes the password of a judge based on the provided ID.", tags = {"put", "admin", "judge"})
    public ResponseEntity<ResponseDTO<JudgeDTO>> changePassword(@RequestBody JudgeDTO judgeDTO) {
        Judge judge = this.judgeMapper.toEntity(judgeDTO);
        Judge judgeToBeEdited = this.judgeService.changePassword(judge);
        JudgeDTO judgeDTO1 = this.judgeMapper.toDto(judgeToBeEdited);
        return success("Password changed successfully", judgeDTO1);
    }

    // Admin Role
    @GetMapping("/find/{semesterId}")
    @Operation(summary = "Find Judge", description = "Finds a judge based on the provided ID.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<JudgeDTO>> findJudgeById(@PathVariable Integer semesterId, @RequestParam(name="judgeId") Integer judgeId) {
        Judge judge = this.judgeService.findJudgeById(judgeId, semesterId);
        JudgeDTO judgeDTO = this.judgeMapper.toDto(judge);
        return success("Judge found with id: " + judgeDTO.getId(), judgeDTO);
    }

    // Admin Role
    @PostMapping("/edit")
    @Operation(summary = "Edit Judge", description = "Edits an existing judge based on the provided ID.", tags = {"post", "admin"})
    public ResponseEntity<ResponseDTO<JudgeDTO>> editJudge(@Parameter(description = "ID of the judge to edit", required = true) @RequestParam(name = "judgeId") Integer judgeId, @RequestBody JudgeDTO judgeDTO) {
        Judge judge = this.judgeMapper.toEntity(judgeDTO);
        Judge editedJudge = this.judgeService.editJudge(judgeId, judge);
        JudgeDTO editedJudgeDTO = this.judgeMapper.toDto(editedJudge);
        return success("Judge edited successfully", editedJudgeDTO);
    }

    // Admin Role
    @DeleteMapping("/deleteAll")
    @Operation(summary = "Delete Judge", description = "Deletes a judge based on the provided ID.", tags = {"delete", "admin"})
    public ResponseEntity<ResponseDTO<Void>> deleteJudge(@Parameter(description = "ID of the judge to delete", required = true) @RequestParam(name="judgeId") Integer judgeId) {
        this.judgeService.deleteAccount(judgeId);
        return success("Judge deleted successfully", null);
    }

    // Admin Role
    @DeleteMapping("/resetAll")
    @Operation(summary = "Reset Judges", description = "Resets all judges.", tags = {"delete", "admin"})
    public ResponseEntity<ResponseDTO<Void>> resetAllJudge() {
        this.judgeService.resetjudge();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Admin Role
@PutMapping("/assignProject/{semesterId}")
public ResponseEntity<ResponseDTO<List<JudgeWithAssignedProjectsDTO>>> assignJudgeToProjects(@PathVariable Integer semesterId, @RequestBody List<JudgeWithAssignedProjectsDTO> judgeWithAssignedProjectsReqList) {
    List<JudgeWithAssignedProjectsDTO> judgeWithAssignedProjectsDTOList = this.judgeService.assignJudgeToProjects(judgeWithAssignedProjectsReqList, semesterId);
    return success("Projects assigned successfully", judgeWithAssignedProjectsDTOList);
}


    // Admin Role
    @PutMapping("/unassignProject/{semesterId}")
    @Operation(summary = "Unassign Project from Judge", description = "Removes assignment of a project from a judge.", tags = {"put", "admin"})
    public ResponseEntity<ResponseDTO<Void>> unassignProject(
                                                @PathVariable Integer semesterId,
                                                @Parameter(description = "ID of the judge", required = true) @RequestParam(name="judgeId") Integer judgeId,
                                                @Parameter(description = "ID of the project", required = true) @RequestParam(name="projectId") Integer projectId) {
        this.judgeService.removeAssignedJudge(judgeId, projectId, semesterId);
        return success("Project unassigned successfully", null);
    }

    // Both Roles (Judge is used for checked their assigned projects)
    @GetMapping("/showAssignedProject/{semesterId}")
    @Operation(summary = "Show Assigned Projects", description = "Returns a list of projects assigned to a judge.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<Set<ProjectDTO>>> showAssignedProject(@PathVariable Integer semesterId,
                                                                            @Parameter(description = "ID of the judge", required = true) @RequestParam(name="judgeId") Integer judgeId) {
        Set<Project> projects = this.judgeService.showAssignedProject(judgeId, semesterId);
        Set<ProjectDTO> projectsDTO = projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toSet());
        return success("Assigned project", projectsDTO);
    }

    @GetMapping("/JudgesListProjects/{semesterId}")
    @Operation(summary = "Show Judges List and Assigned Projects", description = "Returns a list of judges and their assigned projects for a given semester.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<JudgesListAndAssignedDTO>>> showJudgesListAndAssignedProjects(@PathVariable Integer semesterId) {
        List<JudgesListAndAssignedDTO> judgesListAndAssignedDTOList = judgeService.showJudgesListAndAssignedProjects(semesterId);
        return success("Judges list and assigned projects", judgesListAndAssignedDTOList);
    }
}
