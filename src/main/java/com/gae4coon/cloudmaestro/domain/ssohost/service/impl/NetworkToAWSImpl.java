package com.gae4coon.cloudmaestro.domain.ssohost.service.impl;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import com.gae4coon.cloudmaestro.domain.ssohost.service.NetworkToAWS;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class NetworkToAWSImpl implements NetworkToAWS {
    @Override

    public List<NodeData> changeNodeSource(List<NodeData> nodeDataList) {
        for (NodeData nodeData : nodeDataList) {
            String node = nodeData.getKey();

            // server, web server
            if (node.contains("Server")) {
                nodeData.setKey("EC2");
                nodeData.setText("EC2");
                nodeData.setSource("/img/AWS_icon/Arch_Compute/Arch_Amazon-EC2_48.svg");
                nodeData.setType("Compute");
            } else if (node.contains("Anti_DDoS")) {
                nodeData.setKey("Shield");
                nodeData.setText("Shield");
                nodeData.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Shield_48.svg");
                nodeData.setType("Security-Identity-Compliance");
            } else if (node.contains("IPS") || node.contains("IDS")) {
                nodeData.setKey("CloudTrail");
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");
            } else if (node.contains("Database")) {
                nodeData.setKey("RDS");
                nodeData.setText("RDS");
                nodeData.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                nodeData.setType("Database");
            }

        }
        return nodeDataList;
    }

    @Override
    public Map<List<NodeData>, List<GroupData>> changeGroupSource(List<NodeData> nodeDataList,List<GroupData> groupDataList) {
        Set<String> groupKey = new HashSet<>();

        for(GroupData groupData:groupDataList){
            String group = groupData.getKey();
            groupKey.add(group);
        }
        int count = 1;

        for(String key:groupKey){
            if(key.contains("Security Group")) continue;
            for(GroupData groupData:groupDataList) {
                if(!groupData.getKey().equals(key)) continue;
                groupData.setKey(key+" Virtual private cloud (VPC) " + count);
                groupData.setText(key+" Virtual private cloud (VPC) " + count);
                groupData.setStroke("rgb(140,79,255)");

                for(NodeData node: nodeDataList) {
                    if (!node.getGroup().equals(key)) continue;
                    node.setGroup(key + " Virtual private cloud (VPC) " + count);
                }
                for(GroupData group: groupDataList) {
                    if (!group.getGroup().equals(key)) continue;
                    group.setGroup(key + " Virtual private cloud (VPC) " + count);
                }
            }
            count++;
        }

        Map<List<NodeData>, List<GroupData>> result = new HashMap<>();
        result.put(nodeDataList, groupDataList);

        return result;
    }
    @Override

    public List<LinkData> changeLinkSource(List<LinkData> linkDataList) {
        for (LinkData linkData : linkDataList) {
            String node = linkData.getFrom();
            String value;

            // server, web server
            if (node.contains("Server")) {
                value = node.replace("Server", "EC2");
                linkData.setFrom(value);
            } else if (node.contains("Anti_DDoS")) {
                value = node.replace("Anti_DDoS", "Shield");
                linkData.setFrom(value);
            } else if (node.contains("IPS")) {
                value = node.replace("IPS", "CloudTrail");
                linkData.setFrom(value);
            } else if(node.contains("IDS")){
                value = node.replace("IPS","Shield");
                linkData.setFrom(value);
            } else if (node.contains("Database")) {
                value = node.replace("Database","RDS");
                linkData.setFrom(value);
            }
        }
        return linkDataList;
    }
    @Override

    public HashMap<String, Object> changeAll(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        nodeDataList = changeNodeSource(nodeDataList);
        linkDataList = changeLinkSource(linkDataList);
        changeGroupSource(nodeDataList, groupDataList);

        Map<String, Object> responseBody = new HashMap<>();

        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.addAll(nodeDataList);
        finalDataArray.addAll(groupDataList);

        finalDataArray.removeIf(Objects::isNull);

        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);  // 예시
        responseBody.put("linkDataArray", linkDataList);  // 예시

        HashMap<String, Object> response = new HashMap<>();

        response.put("result", responseBody);

        return response;
    }

}
