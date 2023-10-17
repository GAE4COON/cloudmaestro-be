package com.gae4coon.cloudmaestro.domain.ssohost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GraphLinksModel {
    private String classType;
    private String linkKeyProperty;
    private List<NodeData> nodeDataArray;
    private List<LinkData> linkDataArray;
    private Cost cost;
}
