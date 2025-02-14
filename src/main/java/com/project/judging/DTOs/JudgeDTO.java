package com.project.judging.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudgeDTO {
    private Integer id;
    private String account;
    private String pwd;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String description;
    private SemesterDTO semester;
    private Integer numberOfProject;
}