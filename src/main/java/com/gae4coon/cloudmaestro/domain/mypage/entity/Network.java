package com.gae4coon.cloudmaestro.domain.mypage.entity;


import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "network")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)public class Network {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "networkId")
    private Long networkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member userId;

    @Column(name = "networkFile", nullable = false, length = 256)
    private String networkFile;

    @OneToMany(mappedBy = "networkId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Diagram> diagrams;

    @Builder
    public Network(Long networkId, Member userId, String networkFile, Set<Diagram> diagrams){
        this.networkId = networkId;
        this.userId = userId;
        this.networkFile = networkFile;
        this.diagrams = diagrams;
    }

}
