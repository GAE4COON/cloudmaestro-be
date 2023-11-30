package com.gae4coon.cloudmaestro.domain.requirements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {
    private String name;
    private String function;
    private List<String> availableNode;
    private List<String> serverNode;
    private List<String> zoneRequirements;
}