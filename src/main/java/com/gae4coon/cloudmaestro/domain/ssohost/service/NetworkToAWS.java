package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import io.swagger.v3.oas.models.links.Link;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface NetworkToAWS {
    List<NodeData> changeNodeSource (List<NodeData> nodeDataList);
    Map<List<NodeData>, List<GroupData>> changeGroupSource (List<NodeData> nodeDataList, List<GroupData> groupDataList);
    List<LinkData> changeLinkSource (List<LinkData> linkDataList);

    HashMap<String, Object> changeAll (List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList);


}
