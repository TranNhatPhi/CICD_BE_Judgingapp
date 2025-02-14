package com.project.judging.Repositories;

import com.project.judging.Entities.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Integer> {

    @Query("SELECT j FROM Judge j WHERE j.semester.id = :semesterId AND j.role = 'judge' ORDER BY j.id ASC")
    List<Judge> findAll(Integer semesterId);

    @Query("SELECT j FROM Judge j WHERE j.id =:judgeId AND j.semester.id = :semesterId ORDER BY j.id ASC")
    Optional<Judge> findJudgeBySemesterId(Integer judgeId, Integer semesterId);

    @Query("SELECT j FROM Judge j WHERE j.account = :username")
    Judge findSemesterByUserName(String username);

    @Query("SELECT j FROM Judge j WHERE j.account = :account")
    Optional <Judge> findByAccount(String account);

    @Query("SELECT j from Judge j WHERE j.role = 'admin'")
    Judge findJudgeWithAdminRole();

    @Query("SELECT j from Judge j JOIN j.projects p WHERE p.id = :id ORDER BY j.id ASC")
    List<Judge> findJudgesBelongToProject(Integer id);

    @Query("SELECT j FROM Judge j JOIN MarkingRound m ON m.judge.id = j.id WHERE m.project.id = :projectId AND m.round = 1 AND m.isMarkedByJudge = true")
    List<Judge> findJudgesMarkedProjectRound(Integer projectId);

    @Query("SELECT j FROM Judge j JOIN MarkingRound m ON m.judge.id = j.id WHERE m.project.id = :projectId AND m.project.isRound1Closed= true AND m.round = 2 AND m.isMarkedByJudge = true ORDER BY m.project.id ASC")
    List<Judge> findJudgesMarkedProjectRound2(Integer projectId);

    @Query("SELECT COUNT(j) > 0 FROM Judge j WHERE j.role = :role")
    boolean existsByRole(String role);
}
