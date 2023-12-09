package com.gae4coon.cloudmaestro.domain.ssohost.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.attribute.FileTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkData {
    private String from;
    private String to;
    private int key;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int uniqueLinkId;
    @Builder
    LinkData(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LinkData linkData = (LinkData) obj;
        return Objects.equals(from, linkData.from) && Objects.equals(to, linkData.to);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    public static List<LinkData> uniqueLink(List<LinkData> linkDataList) {
        Set<LinkData> uniqueSet = new HashSet<>(linkDataList);
        return new ArrayList<>(uniqueSet);
    }
}