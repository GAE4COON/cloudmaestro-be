package com.example.EC2.domain.dto;

import lombok.Data;

@Data
public class ApiNameRequest {
    private String platform;
    private String instanceType;
}

