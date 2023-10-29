package com.gae4coon.cloudmaestro.domain.price.entity;
import jakarta.persistence.*;
import lombok.*;


@Data // 이 어노테이션은 @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode를 포함합니다.
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "ec2")
public class MemberEntity {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "name")
    private String name;

    @Column(name = "APIName")
    private String APIName;

    @Column(name = "Vcpu")
    private String Vcpu;

    @Column(name = "InstanceStorage")
    private String InstanceStorage;


    @Column(name = "InstanceMemory")
    private String InstanceMemory;

    @Column(name = "Network")
    private String Network;

    @Column(name = "linuxOnDemand")
    private String LinuxOnDemand;

    @Column(name = "LinuxReserved")
    private String LinuxReserved;

    @Column(name = "LinuxSpot")
    private String LinuxSpot;

    @Column(name = "WindowsOnDemand")
    private String WindowsOnDemand;

    @Column(name = "WindowsReserved")
    private String WindowsReserved;




}
