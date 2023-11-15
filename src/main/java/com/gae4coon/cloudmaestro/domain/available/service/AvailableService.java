package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvailableService {

    public void addALB(int albCount, List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

        String name = "PROD ";

        // ALB Node 추가
        NodeData albNode = createAlbNode(albCount);
        nodeDataList.add(albNode);


        // public 망 - available zone 연결 고리 삭제
        List<LinkData> tempLinkDataList = new ArrayList<>();
        List<LinkData> itemsToRemove = processLinkData(name, albNode, linkDataList, tempLinkDataList);

        linkDataList.removeAll(itemsToRemove);
        linkDataList.addAll(tempLinkDataList);

        // Security Group 안에 Ec2 포함 or Security Group없는 ec2 추가
        List<String> includeEc2Security = processSecurityGroups(nodeDataList, groupDataList, name);
        System.out.println("includeEc2" + includeEc2Security);

        // Ec2 - albNode 연결
        createLinksForEc2(albNode, new HashSet<>(includeEc2Security), tempLinkDataList);

        linkDataList.addAll(tempLinkDataList);

        linkDataList.removeAll(itemsToRemove);
        linkDataList.addAll(tempLinkDataList);

    }

    private List<String> processSecurityGroups(List<NodeData> nodeDataList, List<GroupData> groupDataList, String name) {
        List<String> includeEc2Security = new ArrayList<>();

        for (NodeData nodeData : nodeDataList) {
            // Check if the NodeData is an EC2 instance
            if (nodeData.getText().contains("EC2")) {
                // Check if it belongs to a Security Group
                if (nodeData.getGroup().contains("Security Group")) {
                    String groupName = nodeData.getGroup();

                    // Check if it's part of the current network group
                    for (GroupData group : groupDataList) {
                        if (group.getGroup() != null &&
                                group.getGroup().contains(name) &&
                                group.getKey().contains(groupName)) {

                            includeEc2Security.add(nodeData.getGroup());
                        }
                    }
                } else {
                    // If the EC2 instance doesn't belong to a Security Group
                    includeEc2Security.add(nodeData.getKey());
                }
            }
        }

        return includeEc2Security;
    }

    private void createLinksForEc2(NodeData albNode, Set<String> ec2Set, List<LinkData> tempLinkDataList) {
        for (String ec2 : ec2Set) {
            LinkData data = new LinkData();
            data.setFrom(albNode.getKey());
            data.setTo(ec2);
            data.setKey(-1);
            tempLinkDataList.add(data);
        }
    }

    private List<LinkData> processLinkData(String name, NodeData albNode, List<LinkData> linkDataList, List<LinkData> tempLinkDataList) {
        List<LinkData> itemsToRemove = new ArrayList<>();
        for (LinkData linkdata : linkDataList) {
            if (linkdata.getFrom().contains(name.concat("Public subnet"))) {
                LinkData data = new LinkData();
                data.setFrom(linkdata.getFrom());
                data.setTo(albNode.getKey());
                data.setKey(linkdata.getKey() - 1);
                tempLinkDataList.add(data);
                itemsToRemove.add(linkdata);
            }
        }
        return itemsToRemove;
    }


    private NodeData createAlbNode(int albCount) {
        NodeData albNode = new NodeData();
        albNode.setKey("Elastic Load Balancing Application Load Balancer" + albCount);
        albNode.setText("Elastic Load Balancing Application Load Balancer");
        albNode.setType("Networking-Content-Delivery");
        albNode.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
        albNode.setFigure("Rectangle");
        albNode.setGroup("Availability Zone");
        return albNode;
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

    public LinkData converMapToLinkData(Map<String,Object> data) {
        LinkData linkdata = new LinkData();
        linkdata.setFrom((String) data.get("from"));
        linkdata.setKey((int)data.get("key"));
        linkdata.setTo((String) data.get("to"));
        return linkdata;

    }
    public List<LinkData> unique(List<LinkData> originalList) {
        Set<LinkData> linkDataSet = new HashSet<>();
        for (LinkData link1 : originalList) {
            linkDataSet.add(link1);
        }

        List<LinkData> setlist = new ArrayList<>();
        for (LinkData l : linkDataSet) {
            setlist.add(l);
        }
        return setlist;
    }
}
