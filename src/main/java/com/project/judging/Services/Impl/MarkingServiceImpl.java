package com.project.judging.Services.Impl;

import java.util.*;
import java.util.stream.Collectors;

import com.project.judging.Config.Jwt.JwtUtils;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.MarkingStatusRoundDTO;
import com.project.judging.DTOs.ProjectDTO;
import com.project.judging.Entities.*;
import com.project.judging.Exception.CustomException;
import com.project.judging.Mapper.Impl.ProjectMapper;
import com.project.judging.Repositories.*;
import com.project.judging.Services.MarkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.judging.DTOs.JudgeMarkingDTO.MarkingTotalDtoReq;
import org.springframework.transaction.annotation.Transactional;

    @Service
    public class MarkingServiceImpl implements MarkingService {

        private final CriteriaRepository criteriaRepository;
        private final SemesterRepository semesterRepository;
        private final JudgeRepository judgeRepository;
        private final ProjectRepository projectRepository;
        private final MarkingRoundRepository markingRoundRepository;
        private final ProjectMapper projectMapper;

        private final Logger logger = LoggerFactory.getLogger(MarkingServiceImpl.class);
        private final ProjectServiceImpl projectServiceImpl;

        @Autowired
        public MarkingServiceImpl(CriteriaRepository criteriaRepository,
                                  SemesterRepository semesterRepository,
                                  JudgeRepository judgeRepository,
                                  ProjectRepository projectRepository,
                                  MarkingRoundRepository markingRoundRepository,
                                  ProjectMapper projectMapper,
                                  ProjectServiceImpl projectServiceImpl) {
            this.criteriaRepository = criteriaRepository;
            this.semesterRepository = semesterRepository;
            this.judgeRepository = judgeRepository;
            this.projectRepository = projectRepository;
            this.markingRoundRepository = markingRoundRepository;
            this.projectMapper = projectMapper;
            this.projectServiceImpl = projectServiceImpl;
        }

        @Transactional(readOnly = false)
        public List<MarkingRound> markProjectRound1(Integer semesterId, Integer judgeId, Integer projectId, Map<Integer, Integer> criteriaMarks, String comment) {

            Project project = projectRepository.findById(projectId, semesterId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid project ID: " + projectId));
            Judge judge = judgeRepository.findById(judgeId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid judge ID: " + judgeId));

            if (!judge.getProjects().contains(project) || !project.getJudges().contains(judge)) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "In round 1, the project must be assigned to the judge.");
            }

            List<MarkingRound> markingRounds = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : criteriaMarks.entrySet()) {
                Integer criteriaId = entry.getKey();
                Integer mark = entry.getValue();
                validateMarkRange(mark, 5, 10);

                Criteria criteria = criteriaRepository.findById(criteriaId)
                        .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid criteria ID: " + criteriaId));
                logger.info("Fetched Criteria: {}", criteria);

                // Delete existing marking round for the judge, project, criteria and round 1
                markingRoundRepository.ProjectIdAndRoundAndJudgeIdAndCriteriaIdToBeDelete(projectId, judgeId, criteriaId).stream()
                        .filter(markingRound -> markingRound.getRound() == 1)
                        .forEach(markingRoundRepository::delete);

                Optional<MarkingRound> optionalMarkingRound = markingRoundRepository.findByRoundJudgeIdProjectIdCriteriaId(1, judgeId, projectId, criteriaId);
                MarkingRound markingRound;
                if (optionalMarkingRound.isPresent()) {
                    markingRound = optionalMarkingRound.get();
                    markingRound.setMark(mark);
                    markingRound.setMarkedByJudge(true);
                    markingRound.setDescription(comment);
                } else {
                    markingRound = new MarkingRound();
                    markingRound.setRound(1);
                    markingRound.setJudge(judge);
                    markingRound.setProject(project);
                    markingRound.setCriteria(criteria);
                    markingRound.setMark(mark);
                    markingRound.setMarkedByJudge(true);
                    markingRound.setDescription(comment);
                }
                markingRounds.add(markingRoundRepository.save(markingRound));
            }

            updateProjectAverageMark(judge, project, 1);


            return markingRounds;
        }

        @Transactional(readOnly = false)
        public List<MarkingRound> markingRound2(Integer semesterId, Integer judgeId, Map<Integer, Integer> projectMarks, String comment) {

            Optional<Semester> semester = this.semesterRepository.findById(semesterId);
            if (semester.isEmpty()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semesterId);
            }

            Judge judge = judgeRepository.findById(judgeId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid judge ID: " + judgeId));

            List<MarkingRound> allMarkingRounds = new ArrayList<>();

            projectMarks.forEach((projectId, mark) -> {
                try {
                    validateMarkRange(mark, 1, 5);
                } catch (IllegalArgumentException e) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
                }

                List<Project> top5Projects = projectServiceImpl.getTopProjectsFromRound2InDescendingOrder(semesterId);
                if (top5Projects.stream().noneMatch(project -> project.getId().equals(projectId))) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "Project ID: " + projectId + " is not in top 5 projects.");
                }

                Project project = projectRepository.findById(projectId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invalid project ID: " + projectId));

                Set<Integer> uniqueMarks = new HashSet<>(projectMarks.values());
                if (uniqueMarks.size() < projectMarks.size()) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "Marks should be different for each project.");
                }

                Optional<MarkingRound> optionalMarkingRound = markingRoundRepository.findByRoundJudgeIdProjectId(2, judgeId, projectId);
                MarkingRound markingRound = new MarkingRound();
                if (optionalMarkingRound.isPresent()) {
                    markingRound = optionalMarkingRound.get();
                    markingRound.setMark(mark);
                    markingRound.setDescription(comment);
                    markingRound.setMarkedByJudge(true);
                    markingRound.setDescription(comment);
                } else {
                    markingRound.setRound(2);
                    markingRound.setJudge(judge);
                    markingRound.setProject(project);
                    markingRound.setMark(mark);
                    markingRound.setDescription(comment);
                    markingRound.setMarkedByJudge(true);
                    markingRound.setDescription(comment);
                }
                allMarkingRounds.add(markingRoundRepository.save(markingRound));

                updateProjectAverageMark(judge, project, 2);
            });

            return allMarkingRounds;
        }

        private void validateMarkRange(Integer mark, int min, int max) {
            if (mark < min || mark > max) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Mark must be between range " + min + " and " + max + ".");
            }
        }

//        private void updateProjectAverageMark(Judge judge, Project project, int round) {
//            List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndRound(judge.getId() ,project.getId(), round);
//            double averageMark = 0;
//            if (!markingRounds.isEmpty()) {
//                int totalMark = markingRounds.stream().mapToInt(MarkingRound::getMark).sum();
//                int numberOfJudges = (int) markingRounds.stream().map(MarkingRound::getJudge).distinct().count();
//                averageMark = numberOfJudges > 0 ? (double) totalMark / numberOfJudges : 0;
//
//                // Round to two decimal places first
//                averageMark = Math.round(averageMark * 100.0) / 100.0;
//
//                // Round up the second decimal if it's 5 or more
//                if ((averageMark * 100) % 10 >= 5) {
//                    averageMark = Math.ceil(averageMark * 10.0) / 10.0;
//                } else {
//                    averageMark = Math.floor(averageMark * 10.0) / 10.0;
//                }
//
//                // Now averageMark is rounded to one decimal place
//                if (round == 1) {
//                    project.setAverageMarkV1(averageMark);
//                } else if (round == 2) {
//                    project.setAverageMarkV2(averageMark);
//                }
//            }
//            projectRepository.save(project);
//        }

        private void updateProjectAverageMark(Judge judge, Project project, int round) {
            List<MarkingRound> markingRounds = markingRoundRepository.findByProjectIdAndRound(project.getId(), round);

            double averageMark = 0;
            if (!markingRounds.isEmpty()) {
                // Sum the marks from all marking rounds for the project in this round
                int totalMark = markingRounds.stream().mapToInt(MarkingRound::getMark).sum();

                // Count the distinct judges who have marked this project in this round
                int numberOfJudges = (int) markingRounds.stream()
                        .filter(MarkingRound::isMarkedByJudge)
                        .map(MarkingRound::getJudge)
                        .distinct()
                        .count();

                // Calculate the average mark
                averageMark = numberOfJudges > 0 ? (double) totalMark / numberOfJudges : 0;

                // Round to two decimal places first
                averageMark = Math.round(averageMark * 100.0) / 100.0;

                // Round up the second decimal if it's 5 or more
                if ((averageMark * 100) % 10 >= 5) {
                    averageMark = Math.ceil(averageMark * 10.0) / 10.0;
                } else {
                    averageMark = Math.floor(averageMark * 10.0) / 10.0;
                }

                // One decimal place rounding
                if (round == 1) {
                    project.setAverageMarkV1(averageMark);
                } else if (round == 2) {
                    project.setAverageMarkV2(averageMark);
                }
            }
            projectRepository.save(project);
        }


//    @Transactional(readOnly = true)
//    public List<Map.Entry<String, Integer>> getSortedCriteriaMarks(Integer judgeId, Integer semesterId, List<Project> projects) {
//        List<Map.Entry<String, Integer>> criteriaMarksList = new ArrayList<>();
//
//        for (Project project : projects) {
//            List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterId(judgeId, project.getId(), semesterId);
//
//            for (MarkingRound markingRound : markingRounds) {
//                Criteria criteria = markingRound.getCriteria();
//                if (criteria != null) {
//                    String criteriaName = criteria.getCriteriaName();
//                    Integer mark = markingRound.getMark();
//                    criteriaMarksList.add(Map.entry(criteriaName, mark));
//                } else {
//                    logger.error("Criteria not found for markingRound: {}", markingRound);
//                }
//            }
//        }
//
//        return criteriaMarksList;
//    }

    private List<Map.Entry<String, Integer>> getSortedCriteriaMarks(Integer judgeId, Integer semesterId, List<Project> projects) {
        List<Map.Entry<String, Integer>> criteriaMarksList = new ArrayList<>();

            for (Project project : projects) {
                List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterIdInRound1(judgeId, project.getId(), semesterId); // round 1
                for (MarkingRound markingRound : markingRounds) {
                    Criteria criteria = this.criteriaRepository.findById(markingRound.getCriteria().getId()) // round 1
                            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid criteria ID: " + markingRound.getCriteria().getId()));
                    if (criteria != null) {
                        String criteriaName = criteria.getCriteriaName();
                        Integer mark = markingRound.getMark();
                        criteriaMarksList.add(Map.entry(criteriaName, mark));
                    } else {
                        logger.info("Criteria not found for markingRound: {}", markingRound);
                    }
                }
        }



        return criteriaMarksList;
    }

//        @Transactional(readOnly = true)
//        public List<MarkingTotalDtoReq> getProjectsAssignedToJudgeWithTotalMarks(Integer judgeId, Integer semesterId) {
//            logger.info("Fetching projects assigned to judgeId: {}", judgeId);
//            Judge judge = judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
//                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid judge ID: " + judgeId));
//
//            List<MarkingTotalDtoReq> result = judge.getProjects().stream()
//                    .filter(project -> project.getSemester().getId().equals(semesterId))
//                    .sorted(Comparator.comparing(Project::getId))
//                    .map(project -> {
//                        List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterIdInRound1(judgeId, project.getId(), semesterId);
//                        logger.info("Found {} marking rounds for project {}", markingRounds.size(), project.getId());
//
//                        List<Map.Entry<String, Integer>> criteriaMarksList = getSortedCriteriaMarks(judgeId, semesterId, List.of(project));
//                        logger.info("Criteria marks list size: {}", criteriaMarksList.size());
//
//                        Map<String, Integer> criteriaMarksSortedById = criteriaMarksList.stream()
//                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//
//                        int totalMark = markingRounds.stream()
//                                .filter(markingRound -> markingRound.getRound() == 1)
//                                .mapToInt(MarkingRound::getMark)
//                                .sum();
//                        logger.info("Total mark for project {}: {}", project.getId(), totalMark);
//
//                        boolean isMarkedByJudge = markingRounds.stream()
//                                .anyMatch(markingRound -> markingRound.getRound() == 1 && markingRound.isMarkedByJudge());
//
//                        String comment = markingRounds.stream()
//                                .filter(markingRound -> markingRound.getRound() == 1)
//                                .filter(markingRound -> markingRound.getJudge().equals(judge))
//                                .filter(markingRound -> markingRound.getProject().equals(project))
//                                .map(MarkingRound::getDescription)
//                                .findAny()
//                                .orElse(null);
//
//                        return new MarkingTotalDtoReq(project.getId(), project.getGroupName(), project.getTitle(), project.getDescription(), criteriaMarksSortedById, isMarkedByJudge, totalMark, comment);
//                    })
//                    .collect(Collectors.toList());
//
//            return result;
//        }

//        V3
        @Transactional(readOnly = true)
        public List<MarkingTotalDtoReq> getProjectsAssignedToJudgeWithTotalMarks(Integer judgeId, Integer semesterId) {
            logger.info("Fetching projects assigned to judgeId: {}", judgeId);
            Judge judge = judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid judge ID: " + judgeId));

            List<MarkingTotalDtoReq> result = judge.getProjects().stream()
                    .filter(project -> project.getSemester().getId().equals(semesterId))
                    .sorted(Comparator.comparing(Project::getId))
                    .map(project -> {
                        try {
                            logger.info("Processing project: {}", project.getId());
                            List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterIdInRound1(judgeId, project.getId(), semesterId);
                            logger.info("Found {} marking rounds for project {}", markingRounds.size(), project.getId());

                            markingRounds.forEach(round ->
                                    logger.info("Marking round: id={}, criteriaId={}, mark={}, isMarkedByJudge={}",
                                            round.getId(), round.getCriteria().getId(), round.getMark(), round.isMarkedByJudge())
                            );

                            List<Map.Entry<String, Integer>> criteriaMarksList = getSortedCriteriaMarks(judgeId, semesterId, List.of(project));
                            logger.info("Criteria marks list size: {}", criteriaMarksList.size());

                            Map<String, Integer> criteriaMarksSortedById = criteriaMarksList.stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                            int totalMark = markingRounds.stream()
                                    .filter(markingRound -> markingRound.getRound() == 1)
                                    .mapToInt(MarkingRound::getMark)
                                    .sum();
                            logger.info("Total mark for project {}: {}", project.getId(), totalMark);

                            boolean isMarkedByJudge = markingRounds.stream()
                                    .anyMatch(markingRound -> markingRound.getRound() == 1 && markingRound.isMarkedByJudge());

                            String comment = null;
                            comment = markingRounds.stream()
                                    .filter(markingRound -> markingRound != null && markingRound.getRound() == 1)
                                    .filter(markingRound -> markingRound.getJudge() != null && markingRound.getJudge().equals(judge))
                                    .filter(markingRound -> markingRound.getProject() != null && markingRound.getProject().equals(project))
                                    .map(MarkingRound::getDescription)
                                    .filter(Objects::nonNull)
                                    .findAny()
                                    .orElse(null);

                            logger.info("Finished processing project: {}", project.getId());
                            return new MarkingTotalDtoReq(project.getId(), project.getGroupName(), project.getTitle(), project.getDescription(), criteriaMarksSortedById, isMarkedByJudge, totalMark, comment);
                        } catch (Exception e) {
                            logger.error("Error processing project {}: {}", project.getId(), e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return result;
        }

//        V1
//        private List<Map.Entry<String, Integer>> getSortedCriteriaMarks(Integer judgeId, Integer semesterId, List<Project> projects) {
//            List<Map.Entry<String, Integer>> criteriaMarksList = new ArrayList<>();
//
//            for (Project project : projects) {
//                List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterIdInRound1(judgeId, project.getId(), semesterId); // round 1
//                for (MarkingRound markingRound : markingRounds) {
//                    Criteria criteria = this.criteriaRepository.findById(markingRound.getCriteria().getId()) // round 1
//                            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid criteria ID: " + markingRound.getCriteria().getId()));
//                    if (criteria != null) {
//                        String criteriaName = criteria.getCriteriaName();
//                        Integer mark = markingRound.getMark();
//                        criteriaMarksList.add(Map.entry(criteriaName, mark));
//                        logger.info("Added criteria: {}, mark: {} for project: {}", criteriaName, mark, project.getId());
//                    } else {
//                        logger.warn("Criteria not found for markingRound: {}", markingRound);
//                    }
//                }
//            }
//
//            logger.info("Total criteria marks collected: {}", criteriaMarksList.size());
//            return criteriaMarksList;
//        }
// V2
//        @Transactional(readOnly = true)
//        public List<MarkingTotalDtoReq> getProjectsAssignedToJudgeWithTotalMarks(Integer judgeId, Integer semesterId) {
//            logger.info("Fetching projects assigned to judgeId: {}", judgeId);
//            Judge judge = judgeRepository.findJudgeBySemesterId(judgeId, semesterId)
//                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid judge ID: " + judgeId));
//
//
//            List<MarkingTotalDtoReq> result = judge.getProjects().stream()
//                    .filter(project -> project.getSemester().getId().equals(semesterId))
//                    .sorted(Comparator.comparing(Project::getId))
//                    .map(project -> {
//                        List<MarkingRound> markingRounds = markingRoundRepository.findByJudgeIdAndProjectIdAndSemesterIdInRound1(judgeId, project.getId(), semesterId);
//
//                        // Check if criteriaMarksList is null and handle it
//                        List<Map.Entry<String, Integer>> criteriaMarksList = getSortedCriteriaMarks(judgeId, semesterId, List.of(project));
//                        if (criteriaMarksList == null) {
//                            logger.warn("criteriaMarksList is null for judgeId: {} and projectId: {}", judgeId, project.getId());
//                            criteriaMarksList = new ArrayList<>(); // or handle as needed
//                        }
//
//                        Map<String, Integer> criteriaMarksSortedById = criteriaMarksList.stream()
//                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//
//                        int totalMark = markingRounds.stream()
//                                .filter(markingRound -> markingRound.getRound() == 1)
//                                .mapToInt(MarkingRound::getMark)
//                                .sum();
//
//                        boolean isMarkedByJudge = markingRounds.stream()
//                                .anyMatch(markingRound -> markingRound.getRound() == 1 && markingRound.isMarkedByJudge());
//
//                        String comment = markingRounds.stream()
//                                .filter(markingRound -> markingRound.getRound() == 1)
//                                .filter(markingRound -> markingRound.getJudge().equals(judge))
//                                .filter(markingRound -> markingRound.getProject().equals(project))
//                                .map(markingRound -> {
//                                    logger.info("MarkingRound Description: {}", markingRound.getDescription());
//                                    return markingRound.getDescription();
//                                })
//                                .findAny()
//                                .orElse(null);
//
//                        return new MarkingTotalDtoReq(project.getId(), project.getGroupName(), project.getTitle(), project.getDescription(), criteriaMarksSortedById, isMarkedByJudge, totalMark, comment);
//                    })
//                    .collect(Collectors.toList());
//
//            return result;
//        }

        @Transactional
    public List<MarkingStatusRoundDTO> markingRound1Status(Integer semesterId) {
        List<Judge> judges = this.judgeRepository.findAll(semesterId);
        List<MarkingRound> markingRounds = this.markingRoundRepository.findAllBySemesterId(semesterId);
        List<MarkingStatusRoundDTO> markingStatusRound1DTOList = new ArrayList<>();

        for (Judge judge : judges) {
            int totalMarked = 0;
            List<Project> remainingProjects = new ArrayList<>();

            // Get all projects assigned to the judge
            Set<Project> assignedProjects = judge.getProjects().stream()
                    .filter(project -> project.getSemester().getId().equals(semesterId))
                    .collect(Collectors.toSet());

            for (Project project : assignedProjects) {
                boolean isMarked = false;
                for (MarkingRound markingRound : markingRounds) {
                    if (markingRound.getRound() == 1 &&
                        Objects.equals(markingRound.getJudge().getId(), judge.getId()) &&
                        Objects.equals(markingRound.getProject().getId(), project.getId())) {
                        totalMarked++;
                        isMarked = true;
                        break;
                    }
                }
                if (!isMarked) {
                    remainingProjects.add(project);
                }
            }

            List<ProjectDTO> remainingProjectsDTO = remainingProjects.stream()
                .map(this.projectMapper::toDto)
                .toList();

            markingStatusRound1DTOList.add(new MarkingStatusRoundDTO(
                judge.getId(),
                judge.getFirstName() + " " + judge.getLastName(),
                totalMarked,
                remainingProjectsDTO
            ));
        }

        return markingStatusRound1DTOList;
    }

    @Transactional
    public List<MarkingStatusRoundDTO> markingRound2Status(Integer semesterId) {
        List<Judge> judges = this.judgeRepository.findAll(semesterId);
        List<MarkingRound> markingRounds = this.markingRoundRepository.findAllBySemesterId(semesterId);
        List<MarkingStatusRoundDTO> markingStatusRound1DTOList = new ArrayList<>();

        for (Judge judge : judges) {
            int totalMarked = 0;
            List<Project> remainingProjects = new ArrayList<>();
            for (MarkingRound markingRound : markingRounds) {
                if (markingRound.getRound() == 2 && Objects.equals(markingRound.getJudge().getId(), judge.getId())) {
                    totalMarked++;
                }
                if (markingRound.getRound() == 2 && !Objects.equals(markingRound.getJudge().getId(), judge.getId())) {
                    if (markingRound.getProject().isRound1Closed() && !remainingProjects.contains(markingRound.getProject())) {
                        remainingProjects.add(markingRound.getProject());
                    }
                }
            }

            List<ProjectDTO> remainingProjectsDTO = remainingProjects.stream()
                    .map(this.projectMapper::toDto)
                    .toList();

            MarkingStatusRoundDTO dto = new MarkingStatusRoundDTO(
                    judge.getId(),
                    judge.getFirstName() + " " + judge.getLastName(),
                    totalMarked,
                    remainingProjectsDTO);
            markingStatusRound1DTOList.add(dto);
        }
        return markingStatusRound1DTOList;
    }
}

