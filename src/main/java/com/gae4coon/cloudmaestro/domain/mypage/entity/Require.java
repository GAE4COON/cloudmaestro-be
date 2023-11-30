package com.gae4coon.cloudmaestro.domain.mypage.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "requirement")
public class Require {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "require_id")
    private Long requireId;

    @Column(name = "industrial_id", nullable = false, length = 256)
    private String industrialId;


    @OneToMany(mappedBy = "require", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Diagram> diagrams;

    // 대충 망 별 기능... 어케처리할까

    @Builder
    public Require(Long requireId, String industrialId, Set<Diagram> diagrams){
        this.requireId = requireId;
        this.industrialId = industrialId;
        this.diagrams = diagrams;
    }
}
