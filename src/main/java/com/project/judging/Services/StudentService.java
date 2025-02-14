package com.project.judging.Services;

import com.project.judging.Entities.Student;
import java.util.List;

public interface StudentService {
    List<Student> showAllStudent();
    List<Student> showAllStudentByProjectId(Integer projectId);
    List<Student> searchStudentByName(String name);
    Student createStudent(Student student);
    void assignProjectToStudent(Integer studentId, Integer projectId);
}