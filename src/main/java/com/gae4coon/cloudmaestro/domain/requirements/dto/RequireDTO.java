package com.gae4coon.cloudmaestro.domain.requirements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequireDTO {

    private String industrial;
    private List<String> globalRequirements;
    private List<String> backup;
    private List<Zone> zones;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Zone {
        private String name;
        private String function;
        private List<String> availableNode;
        private List<String> zoneRequirements;
    }
}
