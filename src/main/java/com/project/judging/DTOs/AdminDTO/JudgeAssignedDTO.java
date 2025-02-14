package com.project.judging.DTOs.AdminDTO;

import com.project.judging.DTOs.JudgeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudgeAssignedDTO {
    public Integer projectId;
    public String groupName;
    public String title;
    public Integer numberOfJudges;
    public List<JudgeDTO> judge;
}
