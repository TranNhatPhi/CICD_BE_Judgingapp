package com.project.judging.DTOs.AdminDTO;

import com.project.judging.DTOs.JudgeDTO;
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
public class JudgesListAndAssignedDTO {
    private JudgeDTO judge;
    private Integer numberOfProjects;
    private List<ProjectDTO> projects;
}
