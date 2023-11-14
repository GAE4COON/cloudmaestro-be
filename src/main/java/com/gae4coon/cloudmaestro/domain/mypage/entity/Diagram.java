package com.gae4coon.cloudmaestro.domain.mypage.entity;

import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)@Table(name = "diagram")
public class Diagram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagramId", nullable = false, length = 50, unique = true)
    private Long diagramId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloudId")
    private Cloud cloudId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "networkId")
    private Network networkId;


}
