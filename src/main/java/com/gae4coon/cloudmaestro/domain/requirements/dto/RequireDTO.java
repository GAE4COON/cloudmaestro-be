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
    private List<ZoneDTO> zones;


}
