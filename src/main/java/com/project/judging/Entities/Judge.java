package com.project.judging.Entities;

import com.project.judging.Constant.Pattern;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "judge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Judge {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Judge.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = true, length = 64)
    private String account;

    @Column(nullable = true, length = 64)
    private String pwd;

    @Column(nullable = true, length = 64)
    private String plainPwd;

    @Column(name = "first_name", nullable = true, length = 64)
    private String firstName;

    @Column(nullable = true, length = 64)
    private String lastName;

    @Column(nullable = true, length = 64)
    private String email;

    @Column(nullable = true, length = 64)
    private String phone;

    @Column(nullable = true)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "user_role", nullable = false, length = 64)
    private String role;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "modify_at")
    private LocalDateTime modifyAt;

    @LastModifiedBy
    @Column(name = "modify_by", length = 64)
    private String modifyBy;

    @CreatedBy
    @Column(name = "create_by", length = 64)
    private String createBy;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "judge_project",
            joinColumns = @JoinColumn(name = "judge_ID"),
            inverseJoinColumns = @JoinColumn(name = "project_ID")
    )
    private Set<Project> projects = new HashSet<>();

    public Integer getNumberOfProject() {
        return projects.size();
    }

    public void addProject(Project project) {
        if (project == null) {
            log.error("Attempt to add a null project to the judge");
            return;
        }
        this.projects.add(project);
    }

    public void removeProject(Project project) {
        if (project == null || !projects.contains(project)) {
            log.error("Attempt to remove a non-existent project");
            return;
        }
        projects.remove(project);
        project.getJudges().remove(this);
    }

    public void removeAllProjects() {
        for (Project project : new HashSet<>(projects)) {
            removeProject(project);
        }
    }

    // Email Validation
    public void setEmail(String email) {
        if (!email.matches(Pattern.EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
}
