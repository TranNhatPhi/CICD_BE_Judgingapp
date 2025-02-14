package com.project.judging.Services.Impl;

import com.project.judging.Entities.*;
import com.project.judging.Exception.CustomException;
import com.project.judging.Repositories.*;
import com.project.judging.Services.SemesterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {

    private static final Logger logger = LoggerFactory.getLogger(SemesterServiceImpl.class);
    private final SemesterRepository semesterRepository;
    private final SchoolRepository schoolRepository;
    private final ProjectRepository projectRepository;
    private final JudgeRepository judgeRepository;
    private final CriteriaRepository criteriaRepository;

    public SemesterServiceImpl(SemesterRepository semesterRepository, SchoolRepository schoolRepository, ProjectRepository projectRepository, JudgeRepository judgeRepository, CriteriaRepository criteriaRepository) {
        this.semesterRepository = semesterRepository;
        this.schoolRepository = schoolRepository;
        this.projectRepository = projectRepository;
        this.judgeRepository = judgeRepository;
        this.criteriaRepository = criteriaRepository;
    }


    @Override
    public List<Semester> getAllSemesters(Integer schoolId) {
        return semesterRepository.findAll(schoolId);
    }

    @Override
    public Semester getSemesterById(Integer id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + id));
    }

    @Override
    public Semester createSemester(Semester semester, Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid school ID: " + schoolId));
            Semester newSemester = new Semester();
            newSemester.setSemesterName(semester.getSemesterName());
            newSemester.setEventName(semester.getEventName());
            newSemester.setDescription(semester.getDescription());
            newSemester.setSchool(school);
            newSemester.setYearSemester(semester.getYearSemester());
            String[] criteriaNames = {
                    "Usability, particularly relative to the target audience",
                    "Appropriate use of technologies",
                    "Completeness, coherence",
                    "Innovation in terms of execution",
                    "Communication skills",
                    "Professionalism",
                    "Appearance/presentation of this display",
                    "Ability to answer questions, knowledge of the product"

            };
            List<Criteria> criteria = new ArrayList<>();
            Semester savedSemester = semesterRepository.save(newSemester);
            for (String criteriaName : criteriaNames) {
                Criteria newCriteria = new Criteria();
                newCriteria.setCriteriaName(criteriaName);
                newCriteria.setDescription("Default");
                newCriteria.setSemester(savedSemester);
                criteria.add(newCriteria);
            }

            criteriaRepository.saveAll(criteria);
            return savedSemester;

        } catch (Exception e) {
            logger.error("Error creating semester: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Semester updateSemester(Semester semester) {
        try {
            semesterRepository.findById(semester.getId())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semester.getId()));
            return semesterRepository.save(semester);
        } catch (Exception e) {
            logger.error("Error updating semester: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteSemester(Integer id) {
        try {
            List<Project> projects = projectRepository.findListBySemesterId(id);
            List<Judge> judges = judgeRepository.findAll(id);
            List<Criteria> criteria = criteriaRepository.findAll(id);

            projectRepository.deleteAll(projects);
            judgeRepository.deleteAll(judges);
            criteriaRepository.deleteAll(criteria);
            semesterRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting semester: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Semester> getSemestersBySchoolId(Integer schoolId) {
        return semesterRepository.findBySchoolId(schoolId);
    }
}
