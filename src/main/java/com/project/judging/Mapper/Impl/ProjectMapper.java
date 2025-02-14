package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.ProjectDTO;
import com.project.judging.Mapper.Mapper;
import com.project.judging.Entities.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper implements Mapper<Project, ProjectDTO> {

    private final SemesterMapper semesterMapper;

    public ProjectMapper(SemesterMapper semesterMapper) {
        this.semesterMapper = semesterMapper;
    }

    @Override
    public ProjectDTO toDto(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setGroupName(project.getGroupName());
        projectDTO.setTitle(project.getTitle());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setClient(project.getClient());
        projectDTO.setAverageMarkV1(project.getAverageMarkV1());
        projectDTO.setAverageMarkV2(project.getAverageMarkV2());
        projectDTO.setSemester(project.getSemester() != null
                                            ? semesterMapper.toDto(project.getSemester())
                                            : null);
        projectDTO.setTotalNumberOfJudges(project.getTotalNumberOfJudges());
        projectDTO.setRound1Closed(project.isRound1Closed());
        projectDTO.setRound2Closed(project.isRound2Closed());
        projectDTO.setRank(project.getRank());
        return projectDTO;
    }

    @Override
    public Project toEntity(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setTitle(projectDTO.getTitle());
        project.setGroupName(projectDTO.getGroupName());
        project.setDescription(projectDTO.getDescription());
        project.setClient(projectDTO.getClient());
        return project;
    }
}