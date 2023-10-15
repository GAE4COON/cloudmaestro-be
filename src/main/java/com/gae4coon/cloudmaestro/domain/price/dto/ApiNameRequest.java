package com.gae4coon.cloudmaestro.domain.price.dto;

import lombok.Data;

@Data
public class ApiNameRequest {
    private String platform;
    private String instanceType;
}

