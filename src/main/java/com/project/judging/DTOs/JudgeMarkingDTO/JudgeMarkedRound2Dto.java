package com.project.judging.DTOs.JudgeMarkingDTO;

import com.project.judging.DTOs.ProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudgeMarkedRound2Dto {
    ProjectDTO project;
    Integer totalMark;
    boolean isMarkedBỵJudge;
    String comment;
}
