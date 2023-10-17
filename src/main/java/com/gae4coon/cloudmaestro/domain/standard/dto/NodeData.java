package com.gae4coon.cloudmaestro.domain.standard.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    public NodeData() {}

    public NodeData(NodeData original) {  // 복제 생성자
        this.group = original.group;
        this.key = original.key;
        this.type = original.type;
        this.source = original.source;
        this.loc = original.loc;
        this.text = original.text;
        this.figure = original.figure;
        this.isGroup = original.isGroup;
    }
}



