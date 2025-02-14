package com.project.judging.Repositories;

import com.project.judging.Entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {

    @Query("SELECT s FROM Semester s WHERE s.school.id = :schoolId ORDER BY s.id ASC")
    List<Semester> findAll(Integer schoolId);

    @Query("SELECT s FROM Semester s WHERE s.school.id = :schoolId")
    List<Semester> findBySchoolId(Integer schoolId);
}
