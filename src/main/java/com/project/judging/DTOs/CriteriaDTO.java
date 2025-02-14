package com.project.judging.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CriteriaDTO {
    private Integer id;
    private String criteriaName;
    private String description;
    private SemesterDTO semester;
}
