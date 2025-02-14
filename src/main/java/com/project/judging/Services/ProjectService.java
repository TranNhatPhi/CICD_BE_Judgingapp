package com.project.judging.Services;

import com.project.judging.DTOs.AdminDTO.JudgeAssignedDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkDTO;
import com.project.judging.DTOs.AdminDTO.ProjectMarked.JudgeMarkRound2DTO;
import com.project.judging.Entities.Project;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    List<Project> showAllProject(Integer semesterId);
    List<Project> searchProjectByTitle(String title);
    Project findProjectById(Integer projectId, Integer semesterId);
    Project editProject(Integer projectId, Project project);
    void deleteProject(Integer projectId);
    Project addProject(Project project, Integer semesterId);
    List<Project> addProjects(List<Project> projects, Integer semesterId);
    List<JudgeAssignedDTO> showJudgesAssignedInProject(Integer semesterId);


    // Customization for admin
    void getJudgeMarkGroupByProjectId(Integer projectId, Integer semesterId);
    List<Project> getTopProjectsFromRound1InDescendingOrder(Integer semesterId);
    List<Project> getTopProjectsFromRound2InDescendingOrder(Integer semesterId);
    List<JudgeMarkDTO> ProjectListWithJudgeMarks(Integer semesterId);
    List<JudgeMarkRound2DTO> ProjectListWithJudgeMarksRound2(Integer semesterId);
    List<Project> chooseProjectToBeRanked(List<Integer> projectIds, Integer semesterId);
    List<Project> resetRank(Integer semesterId);
    List<Project> resetRound2(Integer semesterId);
    void importProjectsFromExcel(MultipartFile file, Integer semesterId);
}


