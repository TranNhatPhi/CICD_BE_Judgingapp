package com.project.judging.Services;

import com.project.judging.Entities.Criteria;
import java.util.List;

public interface CriteriaService {
    List<Criteria> showAllCriteria(Integer semesterId);
    Criteria findCriteriaById(Integer criteriaId, Integer semesterId);
    Criteria editCriteria(Integer criteriaId, Criteria update);
    Criteria addCriteria(Criteria criteria, Integer semesterId);
    void deleteCriteria(Integer criteriaId);
}