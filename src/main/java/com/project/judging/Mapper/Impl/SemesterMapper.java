package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.SemesterDTO;
import com.project.judging.Mapper.Mapper;
import com.project.judging.Entities.Semester;
import org.springframework.stereotype.Component;

@Component
public class SemesterMapper implements Mapper<Semester, SemesterDTO> {

    private final SchoolMapper schoolMapper;

    public SemesterMapper(SchoolMapper schoolMapper) {
        this.schoolMapper = schoolMapper;
    }

    @Override
    public SemesterDTO toDto(Semester semester) {
        SemesterDTO semesterDTO = new SemesterDTO();
        semesterDTO.setId(semester.getId());
        semesterDTO.setSemesterName(semester.getSemesterName());
        semesterDTO.setYearSemester(semester.getYearSemester());
        semesterDTO.setTotalNumberOfProjects(semester.getTotalNumberOfProjects());
        semesterDTO.setSchool(semester.getSchool() != null
                                ? schoolMapper.toDto(semester.getSchool())
                                : null);
        semesterDTO.setEventName(semester.getEventName());
        semesterDTO.setDescription(semester.getDescription());
        return semesterDTO;
    }

    @Override
    public Semester toEntity(SemesterDTO semesterDTO) {
        Semester semester = new Semester();
        semester.setSemesterName(semesterDTO.getSemesterName());
        semester.setYearSemester(semesterDTO.getYearSemester());
        semester.setEventName(semesterDTO.getEventName());
        semester.setDescription(semesterDTO.getDescription());
        return semester;
    }
}