package com.project.judging.Mapper.Impl;

import com.project.judging.DTOs.JudgeMarkingDTO.MarkingRound1Dto;
import com.project.judging.DTOs.JudgeMarkingDTO.MarkingRound2Dto;
import com.project.judging.Entities.MarkingRound;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class MarkingRoundMapper {

    public MarkingRound1Dto round1Dto (MarkingRound markingRound) {
        MarkingRound1Dto markingRound1Dto = new MarkingRound1Dto();
        markingRound1Dto.setJudgeId(markingRound.getJudge().getId() != null
                                                    ? markingRound.getJudge().getId()
                                                    : null);
        markingRound1Dto.setProjectId(markingRound.getProject().getId());
        Map<Integer, Integer> criteriaMarks = new HashMap<>();
        criteriaMarks.put(markingRound.getCriteria().getId(), markingRound.getMark());
        markingRound1Dto.setCriteriaMarks(criteriaMarks);
        markingRound1Dto.setComment(markingRound.getDescription());
        return markingRound1Dto;
    }

    public MarkingRound2Dto round2Dto(MarkingRound markingRound) {
        MarkingRound2Dto markingRound2Dto = new MarkingRound2Dto();
        markingRound2Dto.setJudgeId(markingRound.getJudge().getId() != null
                ? markingRound.getJudge().getId()
                : null);
        Map<Integer, Integer> projectMarks = new HashMap<>();
        projectMarks.put(markingRound.getProject().getId(), markingRound.getMark());
        markingRound2Dto.setProjectMarks(projectMarks);
        markingRound2Dto.setComment(markingRound.getDescription());
        return markingRound2Dto;
    }
}