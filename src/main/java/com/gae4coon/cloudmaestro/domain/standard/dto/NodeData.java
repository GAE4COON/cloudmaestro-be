package com.gae4coon.cloudmaestro.domain.standard.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class NodeData {
    private String group;
    private String key;
    private String type;
    private String source;
    private String loc;
    private String text;
    private String figure;
    @JsonIgnore
    private String isGroup;



    // getters and setters
}
