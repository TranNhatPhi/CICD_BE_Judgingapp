package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.SchoolDTO;
import com.project.judging.Entities.School;
import com.project.judging.Mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class SchoolMapper implements Mapper<School, SchoolDTO> {

    @Override
    public SchoolDTO toDto(School school) {
        SchoolDTO schoolDTO = new SchoolDTO();
        schoolDTO.setId(school.getId());
        schoolDTO.setSchoolName(school.getSchoolName());
        schoolDTO.setDescription(school.getDescription());
        return schoolDTO;
    }

    @Override
    public School toEntity(SchoolDTO schoolDTO) {
        School school = new School();
        school.setSchoolName(schoolDTO.getSchoolName());
        school.setDescription(schoolDTO.getDescription());
        return school;
    }
}
