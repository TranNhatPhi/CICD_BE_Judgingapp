package com.project.judging.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "marking_round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MarkingRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int round;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criteria_id")
    private Criteria criteria;

    @ManyToOne
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private int mark;

    @Column(name = "is_marked", nullable = false)
    private boolean isMarkedByJudge = false;

    @Column()
    private String description;

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
}

