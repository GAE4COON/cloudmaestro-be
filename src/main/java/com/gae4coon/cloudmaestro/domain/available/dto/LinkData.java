package com.gae4coon.cloudmaestro.domain.available.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkData {
    private String from;
    private String to;
    private int key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        LinkData linkData = (LinkData) o;

        if (!from.equals(linkData.from)) return false;
        return to.equals(linkData.to);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }
}