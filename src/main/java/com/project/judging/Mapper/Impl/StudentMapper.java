package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.StudentDTO;
import com.project.judging.Mapper.Mapper;
import com.project.judging.Entities.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper implements Mapper<Student, StudentDTO> {

    private final ProjectMapper projectMapper;

    public StudentMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public StudentDTO toDto(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setDescription(student.getDescription());
        studentDTO.setProject(student.getProject() != null
                                ? projectMapper.toDto(student.getProject())
                                : null);
        return studentDTO;
    }

    @Override
    public Student toEntity(StudentDTO studentDTO) {
        Student student = new Student();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setDescription(studentDTO.getDescription());
        return student;
    }
}