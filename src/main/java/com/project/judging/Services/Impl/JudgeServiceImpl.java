package com.project.judging.Services.Impl;

import com.project.judging.DTOs.AdminDTO.JudgeAccountDTO;
import com.project.judging.DTOs.AdminDTO.JudgeWithAssignedProjectsDTO;
import com.project.judging.DTOs.AdminDTO.JudgesListAndAssignedDTO;
import com.project.judging.DTOs.JudgeDTO;
import com.project.judging.Entities.Judge;
import com.project.judging.Entities.MarkingRound;
import com.project.judging.Entities.Project;
import com.project.judging.Entities.Semester;
import com.project.judging.Mapper.Impl.JudgeMapper;
import com.project.judging.Mapper.Impl.ProjectMapper;
import com.project.judging.Repositories.JudgeRepository;
import com.project.judging.Repositories.MarkingRoundRepository;
import com.project.judging.Repositories.ProjectRepository;
import com.project.judging.Repositories.SemesterRepository;
import com.project.judging.Services.JudgeService;
import com.project.judging.Utils.PasswordGenerator;
import com.project.judging.Exception.CustomException;
import com.project.judging.Constant.RoleValiddator;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    private final JudgeRepository judgeRepository;
    private final SemesterRepository semesterRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final MarkingRoundRepository markingRoundRepository;
    private final JudgeMapper judgeMapper;
    private final ProjectMapper projectMapper;

    private final Logger logger = LoggerFactory.getLogger(JudgeServiceImpl.class);


    public JudgeServiceImpl(JudgeRepository judgeRepository,
                            SemesterRepository semesterRepository,
                            ProjectRepository projectRepository,
                            PasswordEncoder passwordEncoder,
                            MarkingRoundRepository markingRoundRepository,
                            JudgeMapper judgeMapper,
                            ProjectMapper projectMapper) {
        this.judgeRepository = judgeRepository;
        this.semesterRepository = semesterRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
        this.markingRoundRepository = markingRoundRepository;
        this.judgeMapper = judgeMapper;
        this.projectMapper = projectMapper;
    }


    @Override
    public String passwordGenerator() {
        return PasswordGenerator.generatePassword();
    }

    @Transactional
    public Judge setInitWithExcelExports(Judge judge, Integer semesterId) {
        // Check the valid semester by ID
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Semester Id: " + semesterId));

        // Initialize the judge account
        String account = judge.getAccount();
        // Encrypt the password and set the judge account
        judge.setAccount(account);
        judge.setPlainPwd(PasswordGenerator.generatePassword());
        judge.setPwd(passwordEncoder.encode(judge.getPlainPwd()));
        judge.setFirstName(judge.getFirstName());
        judge.setLastName(judge.getLastName());
        judge.setEmail(judge.getEmail());
        judge.setPhone(judge.getPhone());
        judge.setDescription(judge.getDescription());
        judge.setSemester(semester);
        judge.setModifyAt(LocalDateTime.now());
        judge.setModifyBy(judge.getModifyBy());
        judge.setCreateBy(judge.getCreateBy());
        judge.setRole("judge");
        return this.judgeRepository.save(judge);
    }

    @Transactional
    public void exportAccountsBySemester(HttpServletResponse httpResponse, Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Semester Id: " + semesterId));

        List<Judge> judges = judgeRepository.findAll(semester.getId());

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Accounts");

        HSSFRow headingRow = hssfSheet.createRow(0);
        headingRow.createCell(0).setCellValue("(Semester) " + semester.getSemesterName() + ": Tradeshow Account List");
        hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4)); // Merge the heading across columns

        // Create the header row
        HSSFRow headerRow = hssfSheet.createRow(1);
        headerRow.createCell(0).setCellValue("Account");
        headerRow.createCell(1).setCellValue("Password");
        headerRow.createCell(2).setCellValue("Name");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Phone");

        int rowNum = 2;
        for (Judge judge : judges) {
            HSSFRow dataRow = hssfSheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(judge.getAccount());
            dataRow.createCell(1).setCellValue(judge.getPlainPwd());
            dataRow.createCell(2).setCellValue(judge.getFirstName() + " " + judge.getLastName());
            dataRow.createCell(3).setCellValue(judge.getEmail());
            dataRow.createCell(4).setCellValue(judge.getPhone());
        }

        try {
            httpResponse.setContentType("application/vnd.ms-excel");
            httpResponse.setHeader("Content-Disposition", "attachment; filename=accounts_semester_" + semester.getSemesterName() + ".xls");
            hssfWorkbook.write(httpResponse.getOutputStream());
            hssfWorkbook.close();
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while writing Excel file");
        }
    }


    @Override
    @Transactional
    public List<JudgeAccountDTO> viewJudgesAccount(Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Semester Id: " + semesterId));
        List<Judge> judges = this.judgeRepository.findAll(semester.getId());
        List<JudgeAccountDTO> judgeAccountDTOList = new ArrayList<>();
        for (Judge judge : judges) {
            JudgeAccountDTO judgeAccountDTO = new JudgeAccountDTO();
            judgeAccountDTO.setAccount(judge.getAccount());
            judgeAccountDTO.setEmail(judge.getEmail());
            judgeAccountDTO.setPhoneNumber(judge.getPhone());
            judgeAccountDTO.setPassword(judge.getPlainPwd());
            judgeAccountDTOList.add(judgeAccountDTO);
        }
        return judgeAccountDTOList;

    }

    @Transactional
    public Judge changePassword(Judge judge) {
        if (judge == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Judge is not found");
        }
        logger.info("Changing password for Judge: {}", judge.getAccount());
        try {
            judge.setPwd(judge.getPwd());
            judgeRepository.save(judge);
            logger.info("Password changed for Judge: {}", judge.getAccount());
        } catch (DataAccessException e) {
            logger.error("Error while changing password for Judge: {}", judge.getAccount(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while changing password for Judge");
        }
        return judge;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Judge> showAllAccount(Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Semester Id: " + semesterId));
        return this.judgeRepository.findAll(semester.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Judge findJudgeById(Integer judgeId, Integer semesterId) {
        return judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Judge Id: " + judgeId));
    }

@Override
@Transactional
public Judge editJudge(Integer judgeId, Judge updatedJudge) {
    Judge judge = this.judgeRepository.findById(judgeId)
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Judge Id: " + judgeId));

    if (judge.getSemester() == null) {
        throw new CustomException(HttpStatus.BAD_REQUEST, "Judge does not have an associated semester");
    }

    judge.setAccount(updatedJudge.getAccount());
    judge.setFirstName(updatedJudge.getFirstName());
    judge.setLastName(updatedJudge.getLastName());
    judge.setEmail(updatedJudge.getEmail());
    judge.setPhone(updatedJudge.getPhone());
    judge.setDescription(updatedJudge.getDescription());

    return this.judgeRepository.save(judge);
}


    @Override
    @Transactional
    public void deleteAccount(Integer judgeId) {
        Judge judge = this.judgeRepository.findById(judgeId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Judge Id: " + judgeId));

        String role = judge.getRole();
        if (role.equalsIgnoreCase("admin")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Cannot delete an admin account");
        }

        try {
            logger.info(role + " account deleted: {}", judge.getAccount());
            judge.removeAllProjects();
            judge.setSemester(null);
            this.judgeRepository.delete(judge);
            logger.info("Judge with ID {} deleted successfully", judgeId);
        } catch (DataAccessException e) {
            logger.error("Error while deleting account: {}", judge.getAccount(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while deleting account");
        }
    }

    @Override
    @Transactional
    public void resetjudge() {
        List<Judge> judges = this.judgeRepository.findAll();

        try {
            logger.info("Resetting all judge accounts");
            for (Judge judge : judges) {
                String role = judge.getRole();
                if (role.equalsIgnoreCase("admin")) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "Cannot delete an admin account");
                }
                logger.info(role + " account deleted: {}", judge.getAccount());
                this.judgeRepository.delete(judge);
            }
            this.judgeRepository.deleteAll();
        } catch (DataAccessException e) {
            logger.error("Error while resetting all judge accounts", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while resetting all judge accounts");
        }
    }

    // Print the List of project assigned to the judge
    // List of variables including projectId, projectName, projectTitle, numberofjudges, judgeid, judgeName

@Override
    @Transactional
    public List<JudgeWithAssignedProjectsDTO> assignJudgeToProjects(List<JudgeWithAssignedProjectsDTO> judgeAssignments, Integer semesterId) {
        List<JudgeWithAssignedProjectsDTO> result = new ArrayList<>();

        for (JudgeWithAssignedProjectsDTO assignment : judgeAssignments) {
            Integer judgeId = assignment.getJudgeId();
            List<Integer> newProjectIds = assignment.getProjectIds();

            Judge judge = this.judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Judge Id: " + judgeId));

            String role = judge.getRole();
            if (role.equalsIgnoreCase(RoleValiddator.roleString("Admin"))) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Cannot assign an admin account to a project");
            }

            Set<Project> currentProjects = judge.getProjects();
            List<Integer> currentProjectIds = currentProjects.stream().map(Project::getId).toList();

            List<Project> newProjects = this.projectRepository.findAllById(newProjectIds, semesterId);

            // Determine projects to remove and add
            List<Project> projectsToRemove = currentProjects.stream()
                    .filter(project -> !newProjectIds.contains(project.getId()))
                    .toList();

            List<Project> projectsToAdd = newProjects.stream()
                    .filter(project -> !currentProjectIds.contains(project.getId()))
                    .toList();

            for (Project project : projectsToRemove) {
                judge.removeProject(project);
                project.removeJudge(judge);
                projectRepository.save(project);

                // Delete marking rounds (*note)
                List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterIdInRound1(judgeId, project.getId(), semesterId);
                markingRoundRepository.deleteAll(markingRounds);

                updateAverageMarkV1(project);
            }

            // Add judge to new projects
            for (Project project : projectsToAdd) {
                if (judge.getNumberOfProject() < 25) {
                    judge.addProject(project);
                    project.addJudge(judge);
                    projectRepository.save(project);
                } else {
                    logger.info("Judge {} already assigned to 25 projects", judgeId);
                    throw new CustomException(HttpStatus.BAD_REQUEST, "Judge already assigned to 25 projects");
                }
            }

            judgeRepository.save(judge);
            result.add(new JudgeWithAssignedProjectsDTO(judgeId, newProjectIds));
            logger.info("Projects assigned to Judge: {}", result);
        }

        return result;
    }

    private void updateAverageMarkV1(Project project) {
        List<MarkingRound> markingRounds = markingRoundRepository.findByProjectId(project.getId());

        if (markingRounds.isEmpty()) {
            project.setAverageMarkV1(0.0);
        } else {
            double totalMark = 0;
            long numberOfJudges = markingRounds.stream()
                    .filter(MarkingRound::isMarkedByJudge)
                    .map(MarkingRound::getJudge)
                    .distinct()
                    .count();

            if (numberOfJudges > 0) {
                totalMark = markingRounds.stream()
                        .filter(MarkingRound::isMarkedByJudge)
                        .mapToInt(MarkingRound::getMark)
                        .sum();

                double averageMark = totalMark / numberOfJudges;

                averageMark = Math.round(averageMark * 100.0) / 100.0;

                project.setAverageMarkV1(averageMark);
            } else {
                project.setAverageMarkV1(0.0);
            }
        }

        projectRepository.save(project);
    }


    @Override
    @Transactional
    public void removeAssignedJudge(Integer judgeId, Integer projectId, Integer semesterId) {
        Judge judge = this.judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Judge Id: " + judgeId));

        Project project = this.projectRepository.findById(projectId, semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Project not found"));

        try {
            judge.getProjects().remove(project);
            project.getJudges().remove(judge);
            judgeRepository.save(judge);
            projectRepository.save(project);
            MarkingRound markingRound = this.markingRoundRepository.findByJudgeAndProject(judgeId, projectId);
            markingRoundRepository.delete(markingRound);
        } catch (DataAccessException e) {
            logger.error("Error while removing judge from project", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while removing judge from project");
        }
    }

 @Transactional
public List<Project> projectTobeRound2(List<Integer> projectIds, Integer semesterId) {
    List<Project> allProjects = projectRepository.findAll();

    for (Project project : allProjects) {
        project.setRound1Closed(false);
        project.setAverageMarkV2(0);
        markingRoundRepository.deleteAll(markingRoundRepository.findByJudgeIdAndProjectIdInRound2(project.getId(), 2));
    }

    List<Project> round2Projects = projectRepository.findAllById(projectIds);
    for (Project project : round2Projects) {
        projectRepository.findById(project.getId(), semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Project not found"));
        project.setRound1Closed(true);
        logger.info("Project {} is now in Round 2", project.getId());
        logger.info("Check if project is in Round 2: {}", project.isRound1Closed());

        projectRepository.save(project);
    }

    // send a notification using websocket to all judges
//    JSON.stringify{
//      round1Closed: true,
//      round2Closed: false,
//      eventTitle: "",
//     eventDescription: ""
//     }
     // TODO:
     // listJudge: password - no longer decrypt
     // app/config: tradeshow title, tradeshow description
     //TODO: notification;
    return round2Projects;
}

    @Override
    @Transactional(readOnly = true)
    public Set<Project> showAssignedProject(Integer judgeId, Integer semesterId) {
        Judge judge = this.judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Judge Id: " + judgeId));
        return judge.getProjects();
    }

    @Transactional
    public List<JudgesListAndAssignedDTO> showJudgesListAndAssignedProjects(Integer semesterId) {
        List<Judge> judges = this.judgeRepository.findAll(semesterId);
        List<JudgeDTO> judgeDTOList = judges.stream()
                .map(this.judgeMapper::toDto)
                .toList();
        List<JudgesListAndAssignedDTO> judgesListAndAssignedDTOList = new ArrayList<>();
        for (JudgeDTO judge : judgeDTOList) {
            JudgesListAndAssignedDTO judgesListAndAssignedDTO = new JudgesListAndAssignedDTO();
            judgesListAndAssignedDTO.setJudge(judge);
            judgesListAndAssignedDTO.setNumberOfProjects(judge.getNumberOfProject());
            judgesListAndAssignedDTO.setProjects(judges.stream()
                    .filter(j -> j.getId().equals(judge.getId()))
                    .flatMap(j -> j.getProjects().stream())
                    .sorted(Comparator.comparing(Project::getId))
                    .map(projectMapper::toDto)
                    .collect(Collectors.toList()));
            judgesListAndAssignedDTOList.add(judgesListAndAssignedDTO);
        }
        return judgesListAndAssignedDTOList;
    }
}
