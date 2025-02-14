package com.project.judging.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SemesterDTO {
    private Integer id;
    private String semesterName;
    private Integer yearSemester;
    private Integer totalNumberOfProjects;
    private String eventName;
    private String description;
    private SchoolDTO school;
}