package com.gae4coon.cloudmaestro.domain.ssohost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.attribute.FileTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkData {
    private String from;
    private String to;
    private int key;
    @Builder
    LinkData(String from, String to) {
        this.from = from;
        this.to = to;
    }

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