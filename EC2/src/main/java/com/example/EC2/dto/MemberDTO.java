package com.example.EC2.dto;
import com.example.EC2.entity.MemberEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private int id;
    private String name;
    private String APIName;
    private String InstanceMemory;
    private String Network;
    private String LinuxOnDemand;
    private String LinuxReserved;
    private String LinuxSpot;
    private String WindowsOnDemand;
    private String WindowsReserved;

    public MemberDTO(MemberEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.APIName = entity.getAPIName();
        this.InstanceMemory = entity.getInstanceMemory();
        this.Network = entity.getNetwork();
        this.LinuxOnDemand = entity.getLinuxOnDemand();
        this.LinuxReserved = entity.getLinuxReserved();
        this.LinuxSpot = entity.getLinuxSpot();
        this.WindowsOnDemand = entity.getWindowsOnDemand();
        this.WindowsReserved = entity.getWindowsReserved();
    }

}

