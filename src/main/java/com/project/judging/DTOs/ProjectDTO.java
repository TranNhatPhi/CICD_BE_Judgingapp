package com.project.judging.DTOs;

import com.project.judging.Entities.Ranking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectDTO {
    private Integer id;
    private String groupName;
    private String title;
    private String description;
    private String client;
    private Double averageMarkV1;
    private Double averageMarkV2;
    private boolean isRound1Closed;
    private boolean isRound2Closed;
    private SemesterDTO semester;
    private Integer totalNumberOfJudges;
    private Ranking rank;
}