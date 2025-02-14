package com.project.judging.DTOs.JudgeMarkingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MarkingRound1Dto {
    private Integer judgeId;
    private Integer projectId;
    private Map<Integer, Integer> criteriaMarks;
    private String comment;
}