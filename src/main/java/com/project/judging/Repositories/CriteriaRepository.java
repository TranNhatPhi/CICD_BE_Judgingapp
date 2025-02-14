package com.project.judging.Repositories;

import com.project.judging.Entities.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Integer> {

     @Query("SELECT c FROM Criteria c WHERE c.semester.id = :semesterId ORDER BY c.id ASC")
     List<Criteria> findAll(Integer semesterId);

     @Query("SELECT c FROM Criteria c WHERE c.id = :criteriaId AND c.semester.id = :semesterId ORDER BY c.id ASC")
     Optional<Criteria> findById(Integer criteriaId, Integer semesterId);
}
