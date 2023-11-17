package com.gae4coon.cloudmaestro.domain.ssohost.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
public class NodeData {
    private String text;
    private String type;
    private String key;
    private String source;
    private String loc;
    @JsonIgnore
    private String group;
    @JsonIgnore
    private Boolean isGroup;
    @JsonIgnore
    private String stroke;
    @JsonIgnore
    private String figure;
}