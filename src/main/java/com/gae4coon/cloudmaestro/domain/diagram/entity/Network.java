package com.gae4coon.cloudmaestro.domain.diagram.entity;


import com.gae4coon.cloudmaestro.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "network")
@Data
public class Network {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "networkId")
    private Long networkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member userId;

    @Column(name = "network_file", nullable = false, length = 256)
    private String networkFile;

    @OneToMany(mappedBy = "networkId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Diagram> diagrams;

}
