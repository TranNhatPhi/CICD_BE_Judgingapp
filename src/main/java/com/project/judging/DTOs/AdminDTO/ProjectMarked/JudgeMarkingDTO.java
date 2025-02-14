package com.project.judging.DTOs.AdminDTO.ProjectMarked;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudgeMarkingDTO {
    private String judgeName;
    private Integer groupId;
    private String groupName;
    private String title;
    private Integer mark;
}
