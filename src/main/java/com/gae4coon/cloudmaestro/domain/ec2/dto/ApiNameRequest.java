package com.gae4coon.cloudmaestro.domain.ec2.dto;

import lombok.Data;

@Data
public class ApiNameRequest {
    private String platform;
    private String instanceType;
}

