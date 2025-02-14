package com.project.judging.DTOs.JudgeMarkingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MarkProjectDtoReq {
    private Integer round;
    private Integer judgeId;
    private Integer projectId;
    private Integer criteriaId;
    private Integer mark;
}
