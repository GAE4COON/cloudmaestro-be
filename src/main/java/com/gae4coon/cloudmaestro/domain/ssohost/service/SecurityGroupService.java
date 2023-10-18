package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;

import java.util.List;
import java.util.Map;

public interface SecurityGroupService {
    Map<List<GroupData>, List<NodeData>> addSecurityGroup(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList);
    Map<List<GroupData>,List<NodeData>> modifySecurityGroupLink(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList);

    Map<List<GroupData>,List<NodeData>> excludeNode(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList);


}