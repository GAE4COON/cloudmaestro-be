package com.gae4coon.cloudmaestro.domain.ssohost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GraphLinksModel {
    @JsonProperty("class")
    private String className;
    private String linkKeyProperty;
    private List<NodeData> nodeDataArray;
    private List<LinkData> linkDataArray;
    private Map<String, Object> cost;
}