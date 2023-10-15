package com.gae4coon.cloudmaestro.domain.ec2.dto;

import lombok.Data;




@Data
public class requestPrice {
    private String Platform;
    private String Instance;
    private String LifeCycle;
}