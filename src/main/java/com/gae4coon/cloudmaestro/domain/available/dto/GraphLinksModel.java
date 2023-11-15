package com.gae4coon.cloudmaestro.domain.available.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GraphLinksModel {
    @JsonProperty("class")
    private String className;
    private String linkKeyProperty;
    private List<NodeData> nodeDataArray;
    private List<LinkData> linkDataArray;
    private Cost cost;
}