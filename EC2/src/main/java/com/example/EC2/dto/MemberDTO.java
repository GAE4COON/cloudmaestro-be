package com.example.EC2.dto;
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
}
