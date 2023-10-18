package com.gae4coon.cloudmaestro.domain.standard.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkData linkData = (LinkData) o;
        return Objects.equals(from, linkData.from) &&
                Objects.equals(to, linkData.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }


    // getters and setters
}

