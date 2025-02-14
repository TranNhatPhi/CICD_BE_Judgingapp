package com.project.judging.Repositories;

import com.project.judging.Entities.Project;
import com.project.judging.Entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    @Query("SELECT project FROM Project project WHERE project.semester.id =:semesterId ORDER BY project.id ASC")
    List<Project> findAll(Integer semesterId);

    @Query("SELECT project FROM Project project WHERE project.id IN :projectIds AND project.semester.id = :semesterId ORDER BY project.id ASC")
    List<Project> findAllById(List<Integer> projectIds, Integer semesterId);

    @Query("SELECT project FROM Project project WHERE project.id = :projectId AND project.semester.id = :semesterId ORDER BY project.id ASC")
    Optional<Project> findById(Integer projectId, Integer semesterId);

    @Query("SELECT project FROM Project project WHERE project.semester.id = :semesterId ORDER BY project.id ASC")
    List<Project> findListBySemesterId(Integer semesterId);

    @Query("SELECT project FROM Project project WHERE project.title LIKE %:title% ")
    List<Project> findByTitle(String title);

    @Query("SELECT project FROM Project project WHERE project.semester.id = :semesterId ORDER BY project.averageMarkV1 DESC")
    List<Project> findByDescedingOrderRound1(Integer semesterId);

    @Query("SELECT project FROM Project project WHERE project.semester.id =:semesterId AND project.isRound1Closed = true ORDER BY project.averageMarkV2 DESC")
    List<Project> findByDescedingOrderRound2(Integer semesterId);

    @Query("SELECT project FROM Project project WHERE project.semester.id = :semesterId AND project.isRound1Closed = true ORDER BY project.id ASC")
    List<Project> findListBySemesterIdInRound2(Integer semesterId);

    @Query("SELECT p FROM Project p WHERE p.semester.id = :semesterId AND p.rank IS NOT NULL ORDER BY p.rank")
    List<Project> findByRank(@Param("semesterId") Integer semesterId);

    @Query("SELECT p FROM Project p WHERE p.semester.id = :semesterId AND p.rank IS NOT NULL AND p.rank = 'FIRST' ORDER BY p.rank")
    Project findFirstRank(Integer semesterId);

    @Query("SELECT p FROM Project p WHERE p.semester.id = :semesterId AND p.rank IS NOT NULL AND p.rank = 'SECOND' ORDER BY p.rank")
    Project findSecondRank(Integer semesterId);

    @Query("SELECT p FROM Project p WHERE p.semester.id = :semesterId AND p.rank IS NOT NULL AND p.rank = 'THIRD' ORDER BY p.rank")
    Project findThirdRank(Integer semesterId);

    @Query("SELECT p FROM Project p WHERE p.semester.id = :semesterId AND p.rank IS NOT NULL AND p.rank = 'FOURTH' ORDER BY p.rank")
    Project findFourthRank(Integer semesterId);

    @Query("SELECT p FROM Project p WHERE p.semester.id = :semesterId AND p.rank IS NOT NULL AND p.rank = 'FIFTH' ORDER BY p.rank")
    Project findFifthRank(Integer semesterId);


}
