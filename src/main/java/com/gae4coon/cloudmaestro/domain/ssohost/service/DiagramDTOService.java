package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;

import java.util.*;

public interface DiagramDTOService {
    Map<String, Object> dtoGenerator(GraphLinksModel graphLinksModel);
    HashMap<String, Object> dtoComplete(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList);
}
