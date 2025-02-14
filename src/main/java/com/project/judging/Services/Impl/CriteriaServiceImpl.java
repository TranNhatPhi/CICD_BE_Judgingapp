package com.project.judging.Services.Impl;

import com.project.judging.Entities.Criteria;
import com.project.judging.Entities.Semester;
import com.project.judging.Exception.CustomException;
import com.project.judging.Exception.ResourceNotFoundException;
import com.project.judging.Repositories.CriteriaRepository;
import com.project.judging.Repositories.SemesterRepository;
import com.project.judging.Services.CriteriaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CriteriaServiceImpl implements CriteriaService {

    private final CriteriaRepository criteriaRepository;
    private final SemesterRepository semesterRepository;
    private final Logger logger = LoggerFactory.getLogger(CriteriaServiceImpl.class);

    public CriteriaServiceImpl(CriteriaRepository criteriaRepository,
                               SemesterRepository semesterRepository) {
        this.criteriaRepository = criteriaRepository;
        this.semesterRepository = semesterRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Criteria> showAllCriteria(Integer semesterId) {
        return criteriaRepository.findAll(semesterId);
    }

    @Override
    @Transactional(readOnly = true)
    public Criteria findCriteriaById(Integer criteriaId, Integer semesterId) {
        return criteriaRepository.findById(criteriaId, semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Criteria not found with ID: " + criteriaId));
    }

    @Override
    @Transactional
    public Criteria editCriteria(Integer criteriaId, Criteria update) {
        Criteria criteria = criteriaRepository.findById(criteriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Criteria not found with ID: " + criteriaId));
        criteria.setCriteriaName(update.getCriteriaName());
        criteria.setDescription(update.getDescription());

        try {
            logger.info("Criteria edited: {}", criteria.getCriteriaName());
            return criteriaRepository.save(criteria);
        } catch (Exception e) {
            logger.error("Error while editing criteria: {}", criteria.getCriteriaName(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while editing criteria: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Criteria addCriteria(Criteria criteria, Integer semesterId) {
        Semester semester = this.semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with ID: " + semesterId));
        try {
            logger.info("Criteria added: {}", criteria.getCriteriaName());
            criteria.setCriteriaName(criteria.getCriteriaName());
            criteria.setDescription(criteria.getDescription());
            criteria.setSemester(semester);
            return criteriaRepository.save(criteria);
        } catch (Exception e) {
            logger.error("Error while adding criteria: {}", criteria.getCriteriaName(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while adding criteria: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteCriteria(Integer criteriaId) {
        Criteria criteria = criteriaRepository.findById(criteriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Criteria not found with ID: " + criteriaId));
        try {
            logger.info("Criteria deleted: {}", criteria.getCriteriaName());
            criteriaRepository.delete(criteria);
            logger.info("Check if Criteria deleted: {}", criteria.getCriteriaName());
        } catch (Exception e) {
            logger.error("Error while deleting criteria: {}", criteria.getCriteriaName(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while deleting criteria: " + e.getMessage());
        }
    }
}
