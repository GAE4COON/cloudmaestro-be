package com.gae4coon.cloudmaestro.domain.rehost.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LinkData {
    @EqualsAndHashCode.Include
    private String from;

    @EqualsAndHashCode.Include
    private String to;

    private int key;
    private String group;

    public LinkData(String from, String to, int key, String group){
        this.from = from;
        this.to = to;
        this.key = key;
        this.group = group;
    }

    public LinkData(LinkData original) {  // 복제 생성자
        this.from = original.from;
        this.to = original.to;
        this.key = original.key;
        this.group = original.group;
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
}
