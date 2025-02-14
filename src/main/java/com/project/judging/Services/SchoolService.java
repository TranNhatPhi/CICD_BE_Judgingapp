package com.project.judging.Services;

import com.project.judging.Entities.School;

import java.util.List;

public interface SchoolService {
    List<School> getAllSchools();
    School getSchoolById(Integer id);
    School createSchool(School school);
    School updateSchool(School school);
    void deleteSchool(Integer id);
//    public List<School> searchSchoolByName(String name);
//    public List<School> searchSchoolByDescription(String description);
}
