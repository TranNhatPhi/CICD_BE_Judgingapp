package com.project.judging.Services.Impl;

import com.project.judging.DTOs.AdminDTO.JudgeAssignedDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeDTOwithMarked;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkRound2DTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkingDTO;
import com.project.judging.DTOs.JudgeDTO;
import com.project.judging.DTOs.JudgeMarkingDTO.JudgeMarkedRound2Dto;
import com.project.judging.DTOs.ProjectDTO;
import com.project.judging.Entities.*;
import com.project.judging.Exception.CustomException;
import com.project.judging.Mapper.Impl.JudgeMapper;
import com.project.judging.Mapper.Impl.ProjectMapper;
import com.project.judging.Repositories.JudgeRepository;
import com.project.judging.Repositories.MarkingRoundRepository;
import com.project.judging.Repositories.ProjectRepository;
import com.project.judging.Repositories.SemesterRepository;
import com.project.judging.Services.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MarkingRoundRepository markingRoundRepository;
    private final JudgeMapper judgeMapper;
    private final SemesterRepository semesterRepository;

    private final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private final JudgeRepository judgeRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              MarkingRoundRepository markingRoundRepository,
                              JudgeMapper judgeMapper,
                              SemesterRepository semesterRepository,
                              JudgeRepository judgeRepository,
                              ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.markingRoundRepository = markingRoundRepository;
        this.judgeMapper = judgeMapper;
        this.semesterRepository = semesterRepository;
        this.judgeRepository = judgeRepository;
        this.projectMapper = projectMapper;
    }

    // Show the List of Projects (BOTH)
    @Override
    @Transactional(readOnly = true)
    public List<Project> showAllProject(Integer semesterId) {
        logger.info("Fetching all projects");
        return projectRepository.findAll(semesterId).stream().toList();
    }

    // Search Project by Project Name (BOTH)
    @Override
    @Transactional(readOnly = true)
    public List<Project> searchProjectByTitle(String title) {
        logger.info("List of Projects by title: {}", title);
        return projectRepository.findByTitle(title);
    }

    // Find Project by Project ID (BOTH)
    @Override
    @Transactional(readOnly = true)
    public Project findProjectById(Integer projectId, Integer semesterId) {
        return projectRepository.findById(projectId, semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid project ID: " + projectId));
    }

    // Edit Project (ADMIN)
    @Override
    @Transactional
    public Project editProject(Integer projectId, Project project) {
        Project projectValid = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid project ID: " + projectId));
        projectValid.setGroupName(project.getGroupName());
        projectValid.setTitle(project.getTitle());
        projectValid.setDescription(project.getDescription());
        projectValid.setClient(project.getClient());
        projectValid.setModifyAt(LocalDateTime.now());
        logger.info("Project edited: {}", project.getTitle());
        return this.projectRepository.save(projectValid);
    }

    // Delete Project (ADMIN)
    @Override
    @Transactional
    public void deleteProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid project ID: " + projectId));
        logger.info("Project deleted: {}", project.getTitle());

        for (Judge judge : project.getJudges()) {
            judge.getProjects().remove(project);
        }

        project.setSemester(null);
        project.setJudges(null);
        projectRepository.delete(project);
    }

    // Add Project (ADMIN)
    @Override
    @Transactional(readOnly = false)
    public Project addProject(Project project, Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semesterId));
        logger.info("Adding Project: {}", project.getTitle());
        project.setGroupName(project.getGroupName());
        List<Project> allProjects = projectRepository.findAll(semesterId);
        for (Project p: allProjects) {
            if (p.getGroupName().equals(project.getGroupName())) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Group name already exists");
            }
        }
        project.setTitle(project.getTitle());
        project.setDescription(project.getDescription());
        project.setClient(project.getClient());
        project.setSemester(semester);
        logger.info("Project added: {}", project.getTitle());
        return this.projectRepository.save(project);
    }

    @Override
    public List<Project> addProjects(List<Project> projects, Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semesterId));
        logger.info("Adding Projects: {}", projects);
        projects.forEach(project -> {
            project.setSemester(semester);
        });
        return this.projectRepository.saveAll(projects);
    }

    @Override
    @Transactional
    public List<JudgeAssignedDTO> showJudgesAssignedInProject(Integer semesterId) {
        List<Project> projects = projectRepository.findListBySemesterId(semesterId);
        List<JudgeAssignedDTO> judgeAssignedDTOList = new ArrayList<>();
        for (Project project : projects) {
            List<Judge> judges = judgeRepository.findJudgesBelongToProject(project.getId());
            judgeAssignedDTOList.add(new JudgeAssignedDTO(
                    project.getId(),
                    project.getGroupName(),
                    project.getTitle(),
                    project.getJudges().size(),
                    judges.stream()
                            .map(this.judgeMapper::toDto)
                            .collect(Collectors.toList())
            ));
        }

        judgeAssignedDTOList.sort(Comparator.comparingInt(JudgeAssignedDTO::getNumberOfJudges).reversed()
                .thenComparing(JudgeAssignedDTO::getProjectId));

        return judgeAssignedDTOList;
    }

    @Override
    @Transactional
    public void getJudgeMarkGroupByProjectId(Integer projectId, Integer semesterId) {
        Project project = projectRepository.findById(projectId, semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid project ID: " + projectId));
        project.getJudges().stream()
                .map(judge -> new JudgeMarkingDTO(
                        judge.getFirstName() + " " + judge.getLastName(),
                        project.getId(),
                        project.getGroupName(),
                        project.getTitle(),
                        markingRoundRepository.findByJudgeIdAndProjectId(judge.getId(), projectId)
                                .stream()
                                .mapToInt(MarkingRound::getMark)
                                .sum()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Project> getTopProjectsFromRound1InDescendingOrder(Integer semesterId) {
        return projectRepository.findByDescedingOrderRound1(semesterId);
    }

    // Show the list of project in round 2 (after admin changing)
    @Transactional
    public List<Project> projectsInRound2(Integer semesterId) {
        List<Project> projects = projectRepository.findAll(semesterId);
        List<Project> projectsInRound2 = new ArrayList<>();
        for (Project project: projects) {
            if (project.isRound1Closed()) {
                projectsInRound2.add(project);
            }
        }
        return projectsInRound2;
    }

    @Transactional(readOnly = true)
    public List<JudgeMarkedRound2Dto> judgeMarkedRound2Dtos(Integer semesterId, Integer judgeId) {
        List<Project> projects = projectRepository.findListBySemesterIdInRound2(semesterId);
        List<JudgeMarkedRound2Dto> judgeMarkedRound2Dtos = new ArrayList<>();
        for (Project project : projects) {
            List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdInRound2(judgeId, project.getId());
            int totalMark = 0;
            boolean isMarkedBỵJudge = false;
            String comment = null;
            for (MarkingRound markingRound : markingRounds) {
                totalMark += markingRound.getMark();
                isMarkedBỵJudge = markingRound.isMarkedByJudge();
                comment = markingRound.getDescription();
            }
            judgeMarkedRound2Dtos.add(new JudgeMarkedRound2Dto(projectMapper.toDto(project), totalMark, isMarkedBỵJudge, comment));
        }
        return judgeMarkedRound2Dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getTopProjectsFromRound2InDescendingOrder(Integer semesterId) {
        return projectRepository.findByDescedingOrderRound2(semesterId);
    }

    @Override
    @Transactional
    public List<JudgeMarkDTO> ProjectListWithJudgeMarks(Integer semesterId) {
        List<JudgeMarkDTO> result = new ArrayList<>();
        List<Project> projects = projectRepository.findAll(semesterId)
                .stream()
                .sorted(Comparator.comparingDouble(Project::getAverageMarkV1).reversed())
                .toList();
        for (Project project : projects) {
            List<JudgeDTO> judgesAssigned = judgeRepository.findJudgesBelongToProject(project.getId())
                    .stream()
                    .map(judgeMapper::toDto)
                    .collect(Collectors.toList());
            List<JudgeDTOwithMarked> judgesMarked = judgeRepository.findJudgesMarkedProjectRound(project.getId())
                    .stream()
                    .map(judge -> judgeMapper.toDtoWithMarked(judge, project.getId()))
                    .collect(Collectors.toList());

            logger.info("Project ID: {}, Judges Assigned: {}, Judges Marked: {}", project.getId(), judgesAssigned.size(), judgesMarked.size());
            result.add(new JudgeMarkDTO(projectMapper.toDto(project), judgesAssigned, judgesMarked));
        }
        return result;
    }

    @Override
    @Transactional
    public List<JudgeMarkRound2DTO> ProjectListWithJudgeMarksRound2(Integer semesterId) {
        List<JudgeMarkRound2DTO> result = new ArrayList<>();
        List<Project> projects = projectRepository.findListBySemesterIdInRound2(semesterId)
                .stream()
                .sorted(Comparator.comparingDouble(Project::getAverageMarkV2).reversed())
                .toList();;
        logger.info("Projects in Round 2: {}", projects.size());
        for (Project project : projects) {
            List<JudgeDTO> judgesMarked = judgeRepository.findJudgesMarkedProjectRound2(project.getId()).stream()
                    .map(judgeMapper::toDto)
                    .collect(Collectors.toList());

            // Sort by judgesMarked from high to low
            projects.stream()
                    .sorted(Comparator.comparingDouble(Project::getAverageMarkV2).reversed())
                    .collect(Collectors.toList());
            logger.info("Project ID: {}, Judges Marked: {}", project.getId(), judgesMarked.size());

            result.add(new JudgeMarkRound2DTO(projectMapper.toDto(project), judgesMarked));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Project> chooseProjectToBeRanked(List<Integer> projectIds, Integer semesterId) {

        List<Project> all = projectRepository.findAll(semesterId);
        for (Project project: all) {
            project.setRank(null);
            project.setRound2Closed(false);
        }

        List <Project> projects = projectRepository.findByDescedingOrderRound2(semesterId);

        for (Integer projectId: projectIds) {
            Project project = projectRepository.findById(projectId, semesterId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid project ID: " + projectId));
            if (!project.isRound1Closed()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Project ID: " + projectId + " is not in round 2");
            }
        }
        List <Project> updatedProjects = new ArrayList<>();

        for (Project project: projects) {
            for (int i = 0; i < projectIds.size(); i++) {
                if (Objects.equals(project.getId(), projectIds.get(i))) {
                    project.setRank(Ranking.values()[i]);
                    project.setRound2Closed(true);
                    updatedProjects.add(project);
                }
            }
        }

        //TODO: notification;
        return updatedProjects;
    }

    @Override
    public List<Project> resetRank(Integer semesterId) {
        List<Project> projects = projectRepository.findAll(semesterId);
        for (Project project: projects) {
            project.setRank(null);
            project.setRound2Closed(false);
        }
        return projects;
    }

    @Override
    @Transactional
    public List<Project> resetRound2(Integer semesterId) {
        List<Project> projects = projectRepository.findAll(semesterId);
        for (Project project: projects) {
            project.setRound1Closed(false);
            project.setRound2Closed(false);
            project.setRank(null);
            projectRepository.save(project);
        }
        return projects;
    }

    public void importProjectsFromExcel(MultipartFile file, Integer semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semesterId));
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Project project = new Project();
                project.setGroupName(getCellValueAsString(currentRow.getCell(0)));
                project.setTitle(getCellValueAsString(currentRow.getCell(1)));
                project.setDescription(getCellValueAsString(currentRow.getCell(2)));
                project.setClient(getCellValueAsString(currentRow.getCell(3)));
                project.setStudent(getCellValueAsString(currentRow.getCell(4)));
                project.setSemester(semester);
                projectRepository.save(project);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportProjectWithRankInOrder(HttpServletResponse httpResponse, Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semesterId));

//        List<Project> projects = projectRepository.findByRank(semesterId);
        Project project1 = this.projectRepository.findFirstRank(semesterId);
        Project project2 = this.projectRepository.findSecondRank(semesterId);
        Project project3 = this.projectRepository.findThirdRank(semesterId);
        Project project4 = this.projectRepository.findFourthRank(semesterId);
        Project project5 = this.projectRepository.findFifthRank(semesterId);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Project with Rank");

        HSSFRow headingRow = hssfSheet.createRow(0);
        headingRow.createCell(0).setCellValue("(Semester) " + semester.getSemesterName() + ": Project With Rank List");
        hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        HSSFRow headerRow = hssfSheet.createRow(1);
        headerRow.createCell(0).setCellValue("RANK");
        headerRow.createCell(1).setCellValue("Group Name");
        headerRow.createCell(2).setCellValue("Title");
        headerRow.createCell(3).setCellValue("Client");
        headerRow.createCell(4).setCellValue("Student");

            HSSFRow dataRow = hssfSheet.createRow(2);
            dataRow.createCell(0).setCellValue(project1.getRank().name());
            dataRow.createCell(1).setCellValue(project1.getGroupName());
            dataRow.createCell(2).setCellValue(project1.getTitle());
            dataRow.createCell(3).setCellValue(project1.getClient());
            dataRow.createCell(4).setCellValue(project1.getStudent());

            HSSFRow dataRow1 = hssfSheet.createRow(3);
            dataRow1.createCell(0).setCellValue(project2.getRank().name());
            dataRow1.createCell(1).setCellValue(project2.getGroupName());
            dataRow1.createCell(2).setCellValue(project2.getTitle());
            dataRow1.createCell(3).setCellValue(project2.getClient());
            dataRow1.createCell(4).setCellValue(project2.getStudent());

            HSSFRow dataRow2 = hssfSheet.createRow(4);
            dataRow2.createCell(0).setCellValue(project3.getRank().name());
            dataRow2.createCell(1).setCellValue(project3.getGroupName());
            dataRow2.createCell(2).setCellValue(project3.getTitle());
            dataRow2.createCell(3).setCellValue(project3.getClient());
            dataRow2.createCell(4).setCellValue(project3.getStudent());

            HSSFRow dataRow3 = hssfSheet.createRow(5);
            dataRow3.createCell(0).setCellValue(project4.getRank().name());
            dataRow3.createCell(1).setCellValue(project4.getGroupName());
            dataRow3.createCell(2).setCellValue(project4.getTitle());
            dataRow3.createCell(3).setCellValue(project4.getClient());
            dataRow3.createCell(4).setCellValue(project4.getStudent());

            HSSFRow dataRow4 = hssfSheet.createRow(6);
            dataRow4.createCell(0).setCellValue(project5.getRank().name());
            dataRow4.createCell(1).setCellValue(project5.getGroupName());
            dataRow4.createCell(2).setCellValue(project5.getTitle());
            dataRow4.createCell(3).setCellValue(project5.getClient());
            dataRow4.createCell(4).setCellValue(project5.getStudent());
        try {
            httpResponse.setContentType("application/vnd.ms-excel");
            httpResponse.setHeader("Content-Disposition", "attachment; filename=projects_semester_" + semester.getSemesterName() + ".xls");
            hssfWorkbook.write(httpResponse.getOutputStream());
            hssfWorkbook.close();
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while writing Excel file");
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }




}
