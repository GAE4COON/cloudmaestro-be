package com.gae4coon.cloudmaestro.domain.standard.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LinkData {
    private String from;
    private String to;
    private int key;
    public LinkData(String from, String to, int key) {
        this.from = from;
        this.to = to;
        this.key = key;
    }
    public LinkData(LinkData original) {  // 복제 생성자
        this.from = original.from;
        this.to = original.to;
        this.key = original.key;
    }

    // getters and setters
}

