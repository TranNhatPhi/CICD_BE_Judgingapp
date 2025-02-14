package com.project.judging.Entities;

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
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_name", nullable = false, length = 256)
    private String groupName;

    @Column(nullable = false, length = 256)
    private String title;

    @Column(length = 10000)
    private String description;

    @Column(length = 256)
    private String client;

    @Column(name = "average_mark_v1", columnDefinition = "integer default 0")
    private double averageMarkV1 = 0.0;

    @Column(name = "average_mark_v2", columnDefinition = "integer default 0")
    private double averageMarkV2 = 0.0;

    @Column(name = "is_round1_closed")
    private boolean isRound1Closed = false;

    @Column(name = "is_round2_closed")
    private boolean isRound2Closed = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank")
    private Ranking rank;

    @Column(name = "student", length = 4096)
    private String student;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "modify_at")
    private LocalDateTime modifyAt;

    @LastModifiedBy
    @Column(name = "modify_by", length = 20)
    private String modifyBy;

    @CreatedBy
    @Column(name = "create_by", length = 20)
    private String createBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToMany(mappedBy = "projects", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Judge> judges = new HashSet<>();

    public Integer getTotalNumberOfJudges() {
        return judges.size();
    }

    public void addJudge(Judge judge) {
        this.judges.add(judge);
    }

    public void addProjectToRound2(Ranking rank) {
        this.isRound1Closed = true;
        this.rank = rank;
    }

    public void removeJudge(Judge judge) {
        this.judges.remove(judge);
        judge.getProjects().remove(this);
    }
}

