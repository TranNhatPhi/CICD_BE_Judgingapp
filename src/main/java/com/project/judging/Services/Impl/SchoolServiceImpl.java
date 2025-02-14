package com.project.judging.Services.Impl;

import com.project.judging.Entities.School;
import com.project.judging.Exception.CustomException;
import com.project.judging.Repositories.SchoolRepository;
import com.project.judging.Services.SchoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {

    private static final Logger logger = LoggerFactory.getLogger(SchoolServiceImpl.class);
    private final SchoolRepository schoolRepository;

    public SchoolServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public List<School> getAllSchools() {
        try {
            return schoolRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving all schools: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public School getSchoolById(Integer id) {
        try {
            return schoolRepository.findById(id)
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid school ID: " + id));
        } catch (Exception e) {
            logger.error("Error retrieving school by ID: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public School createSchool(School school) {
        try {
            return schoolRepository.save(school);
        } catch (Exception e) {
            logger.error("Error creating school: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public School updateSchool(School school) {
        try {
            schoolRepository.findById(school.getId())
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid school ID: " + school.getId()));
            return schoolRepository.save(school);
        } catch (Exception e) {
            logger.error("Error updating school: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteSchool(Integer id) {
        try {
            schoolRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting school: {}", e.getMessage());
            throw e;
        }
    }

//    @Override
//    public List<School> searchSchoolByName(String name) {
//        try {
//            return schoolRepository.findByNameContaining(name);
//        } catch (Exception e) {
//            logger.error("Error searching school by name: {}", e.getMessage());
//            throw e;
//        }
//    }
//
//    @Override
//    public List<School> searchSchoolByDescription(String description) {
//        try {
//            return schoolRepository.findByDescriptionContaining(description);
//        } catch (Exception e) {
//            logger.error("Error searching school by description: {}", e.getMessage());
//            throw e;
//        }
//    }
}