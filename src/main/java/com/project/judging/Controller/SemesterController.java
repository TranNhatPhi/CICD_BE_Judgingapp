package com.project.judging.Controller;

import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.DTOs.SemesterDTO;
import com.project.judging.Entities.Semester;
import com.project.judging.Mapper.Impl.SemesterMapper;
import com.project.judging.Services.SemesterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/semesters")
public class SemesterController extends BaseController {

    private final SemesterService semesterService;
    private final SemesterMapper semesterMapper;

    public SemesterController(SemesterService semesterService, SemesterMapper semesterMapper) {
        this.semesterService = semesterService;
        this.semesterMapper = semesterMapper;
    }


    @GetMapping("/all/{schoolId}")
    public ResponseEntity<ResponseDTO<List<SemesterDTO>>> getAllSemesters(@PathVariable Integer schoolId) {
        List<SemesterDTO> semesterDTOS = semesterService.getAllSemesters(schoolId).stream()
                .map(semesterMapper::toDto)
                .toList();
        return success("All semesters", semesterDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<SemesterDTO>> getSemesterById(@PathVariable Integer id) {
        Semester semester = semesterService.getSemesterById(id);
        return success("Semester found", semesterMapper.toDto(semester));

    }

    @PostMapping("/{schoolId}")
    public ResponseEntity<ResponseDTO<SemesterDTO>> createSemester(@RequestBody SemesterDTO semester, @PathVariable Integer schoolId) {
        Semester newSemester = semesterMapper.toEntity(semester);
        Semester createdSemester = semesterService.createSemester(newSemester, schoolId);
        SemesterDTO semesterDTO = semesterMapper.toDto(createdSemester);
        return success("Semester created", semesterDTO);
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<SemesterDTO>> updateSemester(@RequestBody SemesterDTO semester) {
        Semester newSemester = semesterMapper.toEntity(semester);
        Semester updatedSemester = semesterService.updateSemester(newSemester);
        SemesterDTO semesterDTO = semesterMapper.toDto(updatedSemester);
        return success("Semester updated", semesterDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteSemester(@PathVariable Integer id) {
        semesterService.deleteSemester(id);
        return success("Semester deleted", null);
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ResponseDTO<List<SemesterDTO>>> getSemestersBySchoolId(@PathVariable Integer schoolId) {
        List<SemesterDTO> semesterDTOS = semesterService.getSemestersBySchoolId(schoolId).stream()
                .map(semesterMapper::toDto)
                .toList();
        return success("Semesters found", semesterDTOS);
    }
}