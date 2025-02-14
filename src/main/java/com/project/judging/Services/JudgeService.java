package com.project.judging.Services;

import com.project.judging.DTOs.AdminDTO.JudgeAccountDTO;
import com.project.judging.DTOs.AdminDTO.JudgeWithAssignedProjectsDTO;
import com.project.judging.Entities.Judge;
import com.project.judging.Entities.Project;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JudgeService {

    String passwordGenerator();
    Judge setInitWithExcelExports(Judge judge, Integer semesterId);
    void exportAccountsBySemester(HttpServletResponse httpResponse, Integer semesterId);
    List<JudgeAccountDTO> viewJudgesAccount(Integer semesterId);

    List<Judge> showAllAccount(Integer semesterId);
    Judge findJudgeById(Integer judgeId, Integer semesterId);
    Judge editJudge(Integer judgeId, Judge judge);
    void deleteAccount(Integer judgeId);
    void resetjudge();
    List<JudgeWithAssignedProjectsDTO> assignJudgeToProjects(List<JudgeWithAssignedProjectsDTO> judgeAssignments, Integer semesterId);
    void removeAssignedJudge(Integer judgeId, Integer projectId, Integer semesterId);
    List<Project> projectTobeRound2(List<Integer> projectIds, Integer semesterId);
    Set<Project> showAssignedProject(Integer judgeId, Integer semesterId);

}