package com.gae4coon.cloudmaestro.domain.price.dto;

import lombok.Data;

@Data
public class rdsCostRequest {
    private String dbEngine;
    private String instanceType;
}
