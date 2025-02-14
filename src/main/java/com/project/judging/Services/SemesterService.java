package com.project.judging.Services;


import com.project.judging.Entities.Semester;

import java.util.List;

public interface SemesterService {

    List<Semester> getAllSemesters(Integer schoolId);
    public Semester getSemesterById(Integer id);
    public Semester createSemester(Semester semester, Integer schoolId);
    public Semester updateSemester(Semester semester);
    public void deleteSemester(Integer id);
    public List<Semester> getSemestersBySchoolId(Integer schoolId);
}