package com.project.judging.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "criteria")
@EntityListeners(AuditingEntityListener.class)
public class Criteria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 256)
    private String criteriaName;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

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
