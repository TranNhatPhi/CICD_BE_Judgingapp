package com.project.judging.Controller;

import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.DTOs.StudentDTO;
import com.project.judging.Entities.Student;
import com.project.judging.Mapper.Impl.StudentMapper;
import com.project.judging.Services.Impl.StudentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "Student Management APIs")
public class StudentController extends BaseController{

    private final StudentServiceImpl studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentServiceImpl studentService,
                             StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    // ADMIN ROLE
    @GetMapping("/showAll")
    @Operation(summary = "Show All Students", description = "Returns a list of all students.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<StudentDTO>>> getAllStudents() {
       List<StudentDTO> studentDTOList = studentService.showAllStudent().stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
        return success("All students", studentDTOList);
    }

    // ADMIN ROLE
    @GetMapping("/showByProjectId")
    @Operation(summary = "Show Students by Project ID", description = "Returns students associated with a specific project identified by its ID.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<StudentDTO>>> getStudentsByProjectId(@Parameter(description = "Project ID", required = true) @RequestParam(name = "projectId") Integer projectId) {
        List<StudentDTO> students = studentService.showAllStudentByProjectId(projectId).stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
        return success("All students in project " + projectId, students);
    }

    // ADMIN ROLE
    @GetMapping("/search")
    @Operation(summary = "Search Students by Name", description = "Returns students matching the given name.", tags = {"get", "admin"})
    public ResponseEntity<ResponseDTO<List<StudentDTO>>> searchStudentsByName(@Parameter(description = "Name of Student", required = true) @RequestParam(name = "name") String name) {
        List<StudentDTO> students = studentService.searchStudentByName(name).stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
        return success("Students found with name: " + name, students);
    }

    // ADMIN ROLE
    @PostMapping("/create")
    @Operation(summary = "Create New Student", description = "Creates a new student with the provided details.", tags = {"post", "admin"})
    public ResponseEntity<ResponseDTO<StudentDTO>> createStudent(@Parameter(description = "Student DTO", required = true) @RequestBody StudentDTO studentDTO) {
        Student student = studentMapper.toEntity(studentDTO);
        Student createdStudent = studentService.createStudent(student);
        StudentDTO createdStudentDTO = studentMapper.toDto(createdStudent);
        return success("Student created successfully", createdStudentDTO);
    }

    // ADMIN ROLE
    @PutMapping("/assign")
    @Operation(summary = "Assign Project to Student", description = "Assigns a project to a student based on their IDs.", tags = {"put", "admin"})
    public ResponseEntity<ResponseDTO<Void>> assignProjectToStudent(@Parameter(description = "Student ID", required = true) @RequestParam(name = "studentId") Integer studentId,
                                                                    @Parameter(description = "Project ID", required = true) @RequestParam(name = "projectId") Integer projectId) {
        studentService.assignProjectToStudent(studentId, projectId);
        return success("Project assigned to student", null);
    }
}