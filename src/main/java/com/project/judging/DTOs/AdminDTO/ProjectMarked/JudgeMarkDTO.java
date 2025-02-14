package com.project.judging.DTOs.AdminDTO.ProjectMarked;

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
public class JudgeMarkDTO {
    private ProjectDTO project;
    private List<JudgeDTO> judgesAssigned;
    private List<JudgeDTOwithMarked> judgesMarked;
}
