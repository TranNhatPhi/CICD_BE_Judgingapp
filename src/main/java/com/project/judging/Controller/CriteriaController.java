package com.project.judging.Controller;

import java.util.List;
import java.util.stream.Collectors;

import com.project.judging.Config.Jwt.JwtUtils;
import com.project.judging.DTOs.ResponseDTO.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.judging.DTOs.CriteriaDTO;
import com.project.judging.Mapper.Impl.CriteriaMapper;
import com.project.judging.Entities.Criteria;
import com.project.judging.Services.Impl.CriteriaServiceImpl;

@RestController
@RequestMapping("/api/criteria")
@Tag(name = "Criteria Management", description = "Criteria Management APIs")
public class CriteriaController extends BaseController{

    private final CriteriaServiceImpl criteriaService;
    private final CriteriaMapper criteriaMapper;
    private final JwtUtils jwtUtils;

    public CriteriaController(CriteriaServiceImpl criteriaService,
                              CriteriaMapper criteriaMapper, JwtUtils jwtUtils) {
        this.criteriaService = criteriaService;
        this.criteriaMapper = criteriaMapper;
        this.jwtUtils = jwtUtils;
    }

    // Both Roles
    @GetMapping("/showAllCriteria/{semesterId}")
    @Operation(summary = "Show All Criteria", description = "Returns a list of all criteria.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<CriteriaDTO>>> showAllCriteria(@PathVariable Integer semesterId) {
        List<CriteriaDTO> criteria = this.criteriaService.showAllCriteria(semesterId).stream()
                .map(this.criteriaMapper::toDto)
                .collect(Collectors.toList());
        return success("All criteria", criteria);
    }

    @GetMapping("/showAllCriteria")
    @Operation(summary = "Show All Criteria", description = "Returns a list of all criteria.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<List<CriteriaDTO>>> showAllCriteriaForJudge(@RequestHeader("Authorization") String token) {
        String jwt = jwtUtils.removeBearerTokenFormat(token);
        Integer semesterId = jwtUtils.getSemesterIdFromToken(jwt);
        List<CriteriaDTO> criteria = this.criteriaService.showAllCriteria(semesterId).stream()
                .map(this.criteriaMapper::toDto)
                .collect(Collectors.toList());
        return success("All criteria", criteria);
    }

    // Both Roles
    @GetMapping("/findCriteriaById/{semesterId}")
    @Operation(summary = "Find Criteria by ID", description = "Returns criteria details based on the provided ID.", tags = {"get", "admin", "judge"})
    public ResponseEntity<ResponseDTO<CriteriaDTO>> findCriteriaById(@PathVariable Integer semesterId,
                                                                    @RequestParam(name = "criteriaId") Integer criteriaId) {
        CriteriaDTO criteria = this.criteriaMapper.toDto(this.criteriaService.findCriteriaById(criteriaId, semesterId));
        return success("Criteria found with id: " + criteria.getId(), criteria);
    }

    // Admin Role
    @PutMapping("/editCriteria")
    @Operation(summary = "Edit Criteria", description = "Updates existing criteria based on the provided ID.", tags = {"put", "admin"})
    public ResponseEntity<ResponseDTO<CriteriaDTO>> editCriteria(@Parameter(description = "ID of Criteria to Edit", required = true) @RequestParam(name = "criteriaId") Integer criteriaId,
                                                    @Parameter(description = "Updated Criteria Details", required = true) @RequestBody CriteriaDTO criteriaDTO) {
        Criteria criteriaToBeUpdated = this.criteriaMapper.toEntity(criteriaDTO);
        CriteriaDTO criteria = this.criteriaMapper.toDto(this.criteriaService.editCriteria(criteriaId, criteriaToBeUpdated));
        return success("Criteria updated with id: " + criteria.getId(), criteria);
    }

    // Admin Role
    @PostMapping("/createCriteria/{semesterId}")
    @Operation(summary = "Create Criteria", description = "Creates a new criteria.", tags = {"post", "admin"})
    public ResponseEntity<ResponseDTO<CriteriaDTO>> addCriteria(@PathVariable Integer semesterId,
                                                                @RequestBody CriteriaDTO criteriaDTO) {
        Criteria criteria = this.criteriaMapper.toEntity(criteriaDTO);
        CriteriaDTO criteriaDtoObj = this.criteriaMapper.toDto(this.criteriaService.addCriteria(criteria, semesterId));
        return success("Criteria added with id: " + criteriaDtoObj.getId(), criteriaDtoObj);
    }

    @DeleteMapping("/deleteCriteria")
    @Operation(summary = "Delete Criteria", description = "Deletes criteria based on the provided ID.", tags = {"delete", "admin"})
    public ResponseEntity<ResponseDTO<Void>> deleteCriteria(@Parameter(description = "ID of Criteria to Delete", required = true) @RequestParam(name = "criteriaId") Integer criteriaId) {
        this.criteriaService.deleteCriteria(criteriaId);
        return success("Criteria deleted with id: " + criteriaId, null);
    }
}
