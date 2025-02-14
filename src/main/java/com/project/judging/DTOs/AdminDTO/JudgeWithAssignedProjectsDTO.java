package com.project.judging.DTOs.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudgeWithAssignedProjectsDTO {
    private Integer judgeId;
    private List<Integer> projectIds;
}
