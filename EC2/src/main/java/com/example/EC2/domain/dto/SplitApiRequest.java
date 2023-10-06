package com.example.EC2.domain.dto;

import lombok.Data;




@Data
public class SplitApiRequest {
    private String Platform;
    private String InstanceType;
}