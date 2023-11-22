package com.gae4coon.cloudmaestro.domain.ssohost.dto;

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
        return from.equals(linkData.from) && to.equals(linkData.to);
    }

    @Override
    public int hashCode() {
        return 31 * from.hashCode() + to.hashCode();
    }

    // 이 메소드는 중복된 링크 데이터를 제거합니다.
    public static List<LinkData> uniqueLink(List<LinkData> linkDataList) {
        Set<LinkData> uniqueSet = new HashSet<>(linkDataList);
        return new ArrayList<>(uniqueSet);
    }
}