package com.gae4coon.cloudmaestro.domain.alert.dto;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import lombok.Data;

@Data
public class inputNodeDto {
    String checkOption;
    NodeData newData;
    String diagramData;
}
