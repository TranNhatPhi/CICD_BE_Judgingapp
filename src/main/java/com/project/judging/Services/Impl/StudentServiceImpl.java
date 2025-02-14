package com.project.judging.Services.Impl;

import com.project.judging.Entities.Project;
import com.project.judging.Entities.Student;
import com.project.judging.Repositories.ProjectRepository;
import com.project.judging.Repositories.StudentRepository;

import java.util.List;

import com.project.judging.Services.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ProjectRepository projectRepository;

    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    public StudentServiceImpl(StudentRepository studentRepository,
                              ProjectRepository projectRepository) {
        this.studentRepository = studentRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> showAllStudent() {
        logger.info("Fetching all students");
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> showAllStudentByProjectId(Integer projectId) {
        logger.info("Fetching students by project ID: {}", projectId);
        return studentRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchStudentByName (String name) {
        logger.info("Searching students by name: {}", name);
        return studentRepository.findByName(name);
    }

    @Override
    @Transactional
    public Student createStudent (Student student) {
        logger.info("Creating student: {}", student);
        student.setFirstName(student.getFirstName());
        student.setLastName(student.getLastName());
        student.setDescription(student.getDescription());
        student.setModifyAt(student.getModifyAt());
        try {
            logger.info("Student created: {}", student);
            return studentRepository.save(student);
        } catch (Exception e) {
            logger.error("Error while creating student: {}", student, e);
            return null;
        }
    }

    @Override
    @Transactional
    public void assignProjectToStudent(Integer studentId, Integer projectId) {
        logger.info("Assigning project {} to student {}", projectId, studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + studentId));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID: " + projectId));
                
        student.setProject(project);
        studentRepository.save(student);
        logger.info("Project {} assigned to student {}", projectId, studentId);
    }
}
