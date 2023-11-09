package com.gae4coon.cloudmaestro.domain.diagram.entity;


import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "cloud")
public class Cloud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cloudId", nullable = false, length = 256)

    private Long cloudId;

    // AWS와 User는 ManyToOne 관계임을 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")

    private Member userId;

    @Column(name = "cloud_file", nullable = false, length = 256)
    private String cloudFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requireId")
    private Require requireId;


    // AWS와 Diagram은 OneToMany 관계임을 명시
    @OneToMany(mappedBy = "cloudId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Diagram> diagrams;

}
