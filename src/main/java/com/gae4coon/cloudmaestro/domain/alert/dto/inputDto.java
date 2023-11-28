package com.gae4coon.cloudmaestro.domain.alert.dto;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import lombok.Data;

@Data
public class inputDto {
    String checkOption;
    GroupData newData;
    String diagramData;
}
