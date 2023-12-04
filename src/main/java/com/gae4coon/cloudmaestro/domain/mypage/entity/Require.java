package com.gae4coon.cloudmaestro.domain.mypage.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "requirement")
public class Require {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "require_id")
    private Long requireId;

    private String industrial;
    private boolean backup;
    private String fileName;

}
