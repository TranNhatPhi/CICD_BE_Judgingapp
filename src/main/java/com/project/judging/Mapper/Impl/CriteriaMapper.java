package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.CriteriaDTO;
import com.project.judging.Mapper.Mapper;
import com.project.judging.Entities.Criteria;
import org.springframework.stereotype.Component;

@Component
public class CriteriaMapper implements Mapper<Criteria, CriteriaDTO> {

    private final SemesterMapper semesterMapper;

    public CriteriaMapper(SemesterMapper semesterMapper) {
        this.semesterMapper = semesterMapper;
    }

    @Override
    public CriteriaDTO toDto(Criteria criteria) {
        CriteriaDTO criteriaDTO = new CriteriaDTO();
        criteriaDTO.setId(criteria.getId());
        criteriaDTO.setCriteriaName(criteria.getCriteriaName());
        criteriaDTO.setDescription(criteria.getDescription());
        criteriaDTO.setSemester(criteria.getSemester() != null
                                    ? this.semesterMapper.toDto(criteria.getSemester())
                                    : null);
        return criteriaDTO;
    }

    @Override
    public Criteria toEntity(CriteriaDTO criteriaDTO) {
        Criteria criteria = new Criteria();
        criteria.setCriteriaName(criteriaDTO.getCriteriaName());
        criteria.setDescription(criteriaDTO.getDescription());
        return criteria;
    }
}