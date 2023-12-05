package com.gae4coon.cloudmaestro.domain.ssohost.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class NodeData {
    private String text;
    private String type;
    private String key;
    private String source;
    private String loc;
    private Boolean isGroup;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String group;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stroke;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String figure;

    @Builder
    NodeData(String text, String type, String key, String source, String loc, Boolean isGroup, String group, String stroke, String figure) {
        this.text = text;
        this.type = type;
        this.key = key;
        this.source = source;
        this.group = group;
    }

}