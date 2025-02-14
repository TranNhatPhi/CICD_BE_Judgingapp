package com.project.judging.Repositories;

import com.project.judging.Entities.MarkingRound;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkingRoundRepository extends JpaRepository<MarkingRound, Integer> {

    @Query("SELECT m FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId")
    List<MarkingRound> findByJudgeIdAndProjectId(Integer judgeId, Integer projectId);

    @Query("SELECT m FROM MarkingRound m WHERE m.project.id = :projectId")
    List<MarkingRound> findByProjectId(Integer projectId);

    @Query("SELECT m FROM MarkingRound m WHERE m.round = :round AND m.judge.id = :judgeId AND m.project.id = :projectId AND m.criteria.id = :criteriaId")
    Optional<MarkingRound> findByRoundJudgeIdProjectIdCriteriaId(
        Integer round, Integer judgeId, Integer projectId, Integer criteriaId);

    @Query("SELECT m FROM MarkingRound m WHERE m.project.id = :projectId AND m.round = :round AND m.judge.id = :judgeId")
    List<MarkingRound> findByJudgeIdAndProjectIdAndRound(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId, @Param("round") Integer round);

    @Query("SELECT m FROM MarkingRound m WHERE m.round = :round AND m.judge.id = :judgeId AND m.project.id = :projectId")
    Optional<MarkingRound> findByRoundJudgeIdProjectId(Integer round, Integer judgeId, Integer projectId);

    @Query("SELECT m FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId")
    MarkingRound findByJudgeAndProject(Integer judgeId, Integer projectId);

    @Query("SELECT m FROM MarkingRound m WHERE m.round = 1 AND m.judge.id = :judgeId AND m.project.id = :projectId AND m.project.semester.id = :semesterId ORDER BY m.criteria.id DESC")
    List<MarkingRound> findByJudgeIdAndProjectIdAndSemesterIdInRound1(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId, @Param("semesterId") Integer semesterId);

    @Query("SELECT m FROM MarkingRound m WHERE m.project.semester.id = :semesterId")
    List<MarkingRound> findAllBySemesterId(@Param("semesterId")Integer semesterId);

    @Query("SELECT m FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId AND m.round = 2")
    List<MarkingRound> findByJudgeIdAndProjectIdInRound2(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId);

    @Modifying
    @Query("DELETE FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId AND m.round = :round")
    void deleteByProjectIdAndRound(@Param("projectId") Integer projectId, @Param("round") int round);

    @Query("SELECT m FROM MarkingRound m WHERE m.project.id = :projectId AND m.round = :round")
    List<MarkingRound> findByProjectIdAndRound(Integer projectId, int round);

   @Query("SELECT m FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId AND m.project.semester.id = :semesterId AND m.criteria.id = :criteriaId AND m.round = 1 ORDER BY m.criteria.id DESC")
List<MarkingRound> findByJudgeIdAndProjectIdAndSemesterIdAndCriteriaIdInRound1(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId, @Param("semesterId") Integer semesterId, @Param("criteriaId") Integer criteriaId);

    @Query("SELECT m.description FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId AND m.round = 1 AND m.project.semester.id = :semesterId")
Optional<String> findCommentByJudgeAndProjectInRound1(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId, @Param("semesterId") Integer semesterId);

@Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId AND m.project.semester.id = :semesterId AND m.round = 1")
Boolean existsByJudgeIdAndProjectIdAndSemesterIdInRound1(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId, @Param("semesterId") Integer semesterId);

    @Query("SELECT m FROM MarkingRound m WHERE m.project.id = :projectId AND m.judge.id = :judgeId AND m.criteria.id = :criteriaId")
    List<MarkingRound> ProjectIdAndRoundAndJudgeIdAndCriteriaIdToBeDelete(Integer projectId, Integer judgeId, Integer criteriaId);

   @Query("SELECT SUM(m.mark) FROM MarkingRound m WHERE m.judge.id = :judgeId AND m.project.id = :projectId AND m.round = 1")
Integer getTotalMarkedByJudge(@Param("judgeId") Integer judgeId, @Param("projectId") Integer projectId);
}
