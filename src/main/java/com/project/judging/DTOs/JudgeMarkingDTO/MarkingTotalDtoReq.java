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
public class MarkingTotalDtoReq {
    private Integer projectId;
    private String groupName;
    private String projectTitle;
    private String description;
    private Map<String, Integer> criteriaMark;
    private boolean isMarkedBỵJudge;
    private int totalMark;
    private String comment;
}
