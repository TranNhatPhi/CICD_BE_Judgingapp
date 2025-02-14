package com.project.judging.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "semester")
public class Semester implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "semester_name", length = 50, nullable = false)
    private String semesterName;

    @Column(name = "year_semester", nullable = false)
    private Integer yearSemester;

    @Column(name = "event_name", length = 10000, nullable = true)
    private String eventName;

    @Column(name = "description", length = 10000, nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @OneToMany(mappedBy = "semester", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();

    public void addProject(Project project) {
        projects.add(project);
        project.setSemester(this);
    }

    public Integer getTotalNumberOfProjects() {
        return projects.size();
    }
}

