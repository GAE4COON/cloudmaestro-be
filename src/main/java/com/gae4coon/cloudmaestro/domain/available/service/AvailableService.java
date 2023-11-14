package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AvailableService {

    public void addALB(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

        String name = "PROD";

        // Public Subnet에 해당되는 영역에 넣기
        int ALB = 2;
        int albCount = 0;
        for (int i = 0; i <= ALB; i++) {
            NodeData albNode = new NodeData();
            albNode.setKey("ALB" + albCount);
            albNode.setText("ALB");
            albNode.setType("Networking-Content-Delivery");
            albNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
            nodeDataList.add(albNode);
        }


        // Link 정보 연결 Public Subnet과 Application Balancer 연결

        for (GroupData group : groupDataList) {
            if (group.getKey().contains(name + "Public subnet")) {
                String privateSubnetKey = group.getKey().replace("Public", "Private");
                LinkData link = new LinkData();
                link.setFrom(group.getKey());
                link.setTo(privateSubnetKey);
                linkDataList.add(link);
            }
        }
    }

    public NodeData convertMapToNodeData(Map<String, Object> data) {
        NodeData nodeData = new NodeData();
        nodeData.setText((String) data.get("text"));
        nodeData.setType((String) data.get("type"));
        nodeData.setKey((String) data.get("key"));
        nodeData.setSource((String) data.get("source"));
        nodeData.setLoc((String) data.get("loc"));
        nodeData.setGroup((String) data.get("group"));
        nodeData.setIsGroup((Boolean) data.get("isGroup"));
        nodeData.setStroke((String) data.get("stroke"));
        nodeData.setFigure((String) data.get("figure"));
        return nodeData;

    }

    public GroupData convertMapToGroupData(Map<String, Object> data) {

        GroupData groupData = new GroupData();
        groupData.setKey((String) data.get("key"));
        groupData.setText((String) data.get("text"));
        groupData.setIsGroup((Boolean) data.get("isGroup"));
        groupData.setGroup((String) data.get("group"));
        groupData.setType((String) data.get("type"));
        groupData.setStroke((String) data.get("stroke"));

        return groupData;
    }
}
