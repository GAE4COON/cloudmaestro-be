package com.gae4coon.cloudmaestro.domain.requirements.dto;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GraphLinksModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequireDiagramDTO {
    private RequireDTO requirementData;
    private String diagramData;
    private String fileName;
}
