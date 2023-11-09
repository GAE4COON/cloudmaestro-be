package com.gae4coon.cloudmaestro.domain.diagram.entity;

import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@Table(name = "requirement")
public class Require {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requireId")
    private Long requireId;

    @Column(name = "industrialId", nullable = false, length = 256)
    private String industrialId;


    @OneToMany(mappedBy = "requireId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Cloud> clouds;

    // 대충 망 별 기능... 어케처리할까

}
