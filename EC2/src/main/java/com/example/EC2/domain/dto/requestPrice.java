package com.example.EC2.domain.dto;

import lombok.Data;




@Data
public class requestPrice {
    private String Platform;
    private String Instance;
    private String LifeCycle;
}