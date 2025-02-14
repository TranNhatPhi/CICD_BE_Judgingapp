package com.project.judging.Controller;

import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import com.project.judging.DTOs.SchoolDTO;
import com.project.judging.Entities.School;
import com.project.judging.Mapper.Impl.SchoolMapper;
import com.project.judging.Services.SchoolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schools")
public class SchoolController extends BaseController {

    private final SchoolService schoolService;
    private final SchoolMapper schoolMapper;

    public SchoolController(SchoolService schoolService, SchoolMapper schoolMapper) {
        this.schoolService = schoolService;
        this.schoolMapper = schoolMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<SchoolDTO>>> getAllSchools() {
        List<School> schools = schoolService.getAllSchools();
        List<SchoolDTO> schoolDTOs = schools.stream()
                .map(schoolMapper::toDto)
                .toList();
        return success("All schools", schoolDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<SchoolDTO>> getSchoolById(@PathVariable Integer id) {
        School school = schoolService.getSchoolById(id);
        SchoolDTO schoolDTO = new SchoolDTO(school.getId(), school.getSchoolName(), school.getDescription());
        return success("School found", schoolDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<SchoolDTO>> createSchool(@RequestBody SchoolDTO schoolDTO) {
        School school = schoolMapper.toEntity(schoolDTO);
        School createdSchool = schoolService.createSchool(school);
        SchoolDTO createdSchoolDTO = new SchoolDTO(createdSchool.getId(), createdSchool.getSchoolName(), createdSchool.getDescription());
        return success("School created", createdSchoolDTO);
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<SchoolDTO>> updateSchool(@RequestBody SchoolDTO schoolDTO) {
        School school = schoolMapper.toEntity(schoolDTO);
        School updatedSchool = schoolService.updateSchool(school);
        SchoolDTO updatedSchoolDTO = new SchoolDTO(updatedSchool.getId(), updatedSchool.getSchoolName(), updatedSchool.getDescription());
        return ResponseEntity.ok(new ResponseDTO<>(200, "School updated successfully", updatedSchoolDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteSchool(@PathVariable Integer id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(new ResponseDTO<>(200, "School deleted successfully", null));
    }

//    @GetMapping("/search/name")
//    public ResponseEntity<ResponseDTO<List<SchoolDTO>>> searchSchoolByName(@RequestParam String name) {
//        List<School> schools = schoolService.searchSchoolByName(name);
//        List<SchoolDTO> schoolDTOs = schools.stream()
//                .map(schoolMapper::toDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(new ResponseDTO<>(200, "Schools retrieved successfully", schoolDTOs));
//    }
//
//    @GetMapping("/search/description")
//    public ResponseEntity<ResponseDTO<List<SchoolDTO>>> searchSchoolByDescription(@RequestParam String description) {
//        List<School> schools = schoolService.searchSchoolByDescription(description);
//        List<SchoolDTO> schoolDTOs = schools.stream()
//                .map(schoolMapper::toDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(new ResponseDTO<>(200, "Schools retrieved successfully", schoolDTOs));
//    }
}
