package com.project.judging.DTOs.AdminDTO.ProjectMarked;

import com.project.judging.DTOs.ProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MarkingStatusRoundDTO {
    private Integer judgeId;
    private String name;
    private Integer numberOfProjectsMarked;
    private List<ProjectDTO> remainingProject;
}
