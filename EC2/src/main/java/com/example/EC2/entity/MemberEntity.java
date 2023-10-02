package com.example.EC2.entity;
import jakarta.persistence.*;
import lombok.*;

@Data // 이 어노테이션은 @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode를 포함합니다.
@Entity
@NoArgsConstructor
@AllArgsConstructor
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


    @Override
    public String toString() {
        return "MemberEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", APIName='" + APIName + '\'' +
                ", InstanceMemory='" + InstanceMemory + '\'' +
                ", Network='" + Network + '\'' +
                ", LinuxOnDemand='" + LinuxOnDemand + '\'' +
                ", LinuxReserved='" + LinuxReserved + '\'' +
                ", LinuxSpot='" + LinuxSpot + '\'' +
                ", WindowsOnDemand='" + WindowsOnDemand + '\'' +
                ", WindowsReserved='" + WindowsReserved + '\'' +
                '}';
    }

}
