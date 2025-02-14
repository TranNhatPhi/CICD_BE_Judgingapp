package com.project.judging.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectMarkedDTO {
    private Integer id;
    private Integer totalMark;
    private Integer totalNumberOfMarkingRoundMarked;
    
}
