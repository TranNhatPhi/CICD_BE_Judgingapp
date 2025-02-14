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
public class MarkingRound2Dto {
    private Integer judgeId;
    private Map<Integer, Integer> projectMarks;
    private String comment;
}
