package com.gae4coon.cloudmaestro.domain.rehost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@NoArgsConstructor  // lombok annotation to add a no-args constructor
@Data
public class GraphLinksModel {
    @JsonProperty("class")
    private String className;
    private String linkKeyProperty;
    private List<NodeData> nodeDataArray;
    private List<LinkData> linkDataArray;
    @JsonProperty("ec2")
    private Ec2 ec2;
    @JsonProperty("cost")
    private Map<String,String> cost;
    // getters and setters
}

