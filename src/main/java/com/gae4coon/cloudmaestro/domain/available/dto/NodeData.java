package com.gae4coon.cloudmaestro.domain.available.dto;

import lombok.Data;

@Data
public class NodeData {
    private String text;
    private String type;
    private String key;
    private String source;
    private String loc;
    private String group;
    private Boolean isGroup;
    private String stroke;
    private String figure;
}