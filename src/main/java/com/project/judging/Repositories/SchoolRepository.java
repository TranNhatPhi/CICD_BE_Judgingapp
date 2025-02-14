package com.project.judging.Repositories;

import com.project.judging.Entities.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {

//    @Query("SELECT s FROM School s WHERE s.schoolName = :schoolName")
//    School findBySchoolName(String schoolName);

//    @Query("SELECT s FROM School s WHERE s.schoolName = :schoolName")
//    List<School> findByNameContaining(String name);
//
//    @Query("SELECT s FROM School s WHERE s.description = :description")
//    List<School> findByDescriptionContaining(String description);
}
