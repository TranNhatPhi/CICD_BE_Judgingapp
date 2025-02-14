package com.project.judging.Services.Impl;

import com.project.judging.DTOs.ResponseDTO.APIConfigDTO;
import com.project.judging.Entities.Project;
import com.project.judging.Entities.Semester;
import com.project.judging.Exception.CustomException;
import com.project.judging.Repositories.ProjectRepository;
import com.project.judging.Repositories.SemesterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {

    private final ProjectRepository projectRepository;
    private final SemesterRepository semesterRepository;

    public ConfigService(ProjectRepository projectRepository, SemesterRepository semesterRepository) {
        this.projectRepository = projectRepository;
        this.semesterRepository = semesterRepository;
    }

    public APIConfigDTO getAPIConfig(Integer semesterId) {
        APIConfigDTO apiConfigDTO = new APIConfigDTO();
        List<Project> projects = projectRepository.findAll(semesterId);
        boolean hasRound2Project = projects.stream()
                .anyMatch(Project::isRound1Closed);

        if (hasRound2Project) {
            apiConfigDTO.setRound1Closed(true);
            boolean isRound2Closed = projects.stream()
                    .anyMatch(Project::isRound2Closed);
            apiConfigDTO.setRound2Closed(isRound2Closed);
        } else {
            apiConfigDTO.setRound1Closed(false);
            apiConfigDTO.setRound2Closed(false);
        }
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid semester ID: " + semesterId));
        apiConfigDTO.setEventName(semester.getEventName());
        apiConfigDTO.setDescription(semester.getDescription());
        return apiConfigDTO;
    }


}
