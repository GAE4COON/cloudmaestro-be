package com.gae4coon.cloudmaestro.domain.naindae.service;

import io.swagger.v3.oas.models.links.Link;
import org.springframework.stereotype.Service;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionService {
    public Map<String, Object> getRegion(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        String num="1";
        Map<String, Object> result = new HashMap<>();
        List<NodeData> newNodeDataList = new ArrayList<>();
        List<GroupData> newGroupDataList = new ArrayList<>();
        List<LinkData> newLinkDataList = new ArrayList<>();
        newNodeDataList = modifyNodeDataForNewRegion(nodeDataList);
        newGroupDataList = modifyGroupDataForNewRegion(groupDataList);
        newLinkDataList = modifyLinkDataForNewRegion(linkDataList);
        newNodeDataList = addNode(newNodeDataList, newGroupDataList, newLinkDataList,num);
        newLinkDataList = addLink(newNodeDataList, newGroupDataList, newLinkDataList,num);

        result.put("nodes", newNodeDataList);
        result.put("groups", newGroupDataList);
        result.put("links", newLinkDataList);

        return result;
    }

    public Map<String, Object> getOriginalRegion(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        String num="2";
        Map<String, Object> result = new HashMap<>();

        nodeDataList = addNode(nodeDataList, groupDataList, linkDataList,num);
        linkDataList = addLink(nodeDataList, groupDataList, linkDataList,num);

        result.put("nodes", nodeDataList);
        result.put("groups", groupDataList);
        result.put("links", linkDataList);

        return result;
    }

    public List<LinkData> regionLink(List<NodeData> nodeDataList, List<NodeData> newDataList, List<LinkData> linkDataList) {
        String transit1 = "";
        String transit2 = "";

        for (NodeData node : nodeDataList) {
            if(node.getKey().contains("Transit Gateway")){
                transit1 = node.getKey();
                break;
            }
        }
        for (NodeData newNode : newDataList) {
            if (newNode.getKey().contains("Transit Gateway")) {
                transit2 = newNode.getKey();
                break;
            }
        }

        LinkData newLink = new LinkData();
        newLink.setFrom(transit1);
        newLink.setTo(transit2);
        linkDataList.add(newLink);

        return linkDataList;
    }

    public List<NodeData> modifyNodeDataForNewRegion(List<NodeData> originalNodeDataList) {
        List<NodeData> modifiedList = new ArrayList<>();
        for (NodeData node : originalNodeDataList) {
            NodeData newNode = new NodeData();
            newNode.setText(node.getText());
            String[] locParts = node.getLoc().split(" ");
            double x = Double.parseDouble(locParts[0]);
            double y = Double.parseDouble(locParts[1]) + 1300;
            newNode.setLoc(x + " " + y); // 수정된 좌표 설정
            newNode.setType(node.getType());
            newNode.setSource(node.getSource());
            newNode.setKey("MR-"+node.getKey());
            newNode.setGroup("MR-"+node.getGroup());
            modifiedList.add(newNode);
        }
        return modifiedList;
    }

    public List<GroupData> modifyGroupDataForNewRegion(List<GroupData> originalGroupDataList) {
        List<GroupData> modifiedList = new ArrayList<>();
        for (GroupData group : originalGroupDataList) {
            GroupData newGroup = new GroupData();
            newGroup.setIsGroup(group.getIsGroup());
            newGroup.setText(group.getText());
            newGroup.setType(group.getType());
            newGroup.setKey("MR-"+group.getKey());
            newGroup.setGroup("MR-"+group.getGroup());
            newGroup.setStroke(group.getStroke());
            modifiedList.add(newGroup);
        }
        return modifiedList;
    }

    public List<LinkData> modifyLinkDataForNewRegion(List<LinkData> originalLinkDataList) {
        List<LinkData> modifiedList = new ArrayList<>();
        for (LinkData link : originalLinkDataList) {
            LinkData newLink = new LinkData();
            newLink.setFrom("MR-"+link.getFrom());
            newLink.setTo("MR-"+link.getTo());
            modifiedList.add(newLink);
        }
        return modifiedList;
    }

    public List<NodeData> addNode(List<NodeData> nodeDataList,  List<GroupData> groupDataList, List<LinkData> linkDataList, String num){
        Map<String, Object> result = new HashMap<>();
        int attachmentCount = 1;
        int transitCount = 1;
        List<String> availabilityGroupKeys = new ArrayList<>();
        List<String> regionGroupKeys = new ArrayList<>();

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Availability")) {
                availabilityGroupKeys.add(group.getKey());
            }
        }

        for (String availabilityGroupKey : availabilityGroupKeys) {
            NodeData attachment = new NodeData();
            attachment.setKey("VPC Elastic Network Interface"+num);
            attachment.setText("VPC Elastic Network Interface");
            attachment.setLoc("loc");
            attachment.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Amazon-VPC_Elastic-Network-Interface_48.svg");
            attachment.setType("Networking-Content-Delivery");
            attachment.setGroup(availabilityGroupKey);
            nodeDataList.add(attachment);
            attachmentCount++;
        }

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Region")) {
                regionGroupKeys.add(group.getKey());
            }
        }

        for (String regionGroupKey : regionGroupKeys) {
            NodeData transit = new NodeData();
            transit.setKey("Transit Gateway"+num);
            transit.setText("Transit Gateway");
            transit.setLoc("loc");
            transit.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_AWS-Transit-Gateway_48.svg");
            transit.setType("Networking-Content-Delivery");
            transit.setGroup(regionGroupKey);
            nodeDataList.add(transit);
            transitCount++;
        }
            return nodeDataList;
    }

    public List<LinkData> addLink(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList, String num) {
        List<String> availabilityGroupKeys = new ArrayList<>();
        List<String> vpcGroupKeys = new ArrayList<>();

//        for (GroupData group : groupDataList) {
//            if (group.getKey().contains("Availability")) {
//                availabilityGroupKeys.add(group.getKey());
//            }
//        }
//        for (String availabilityGroupKey : availabilityGroupKeys){
//            for (GroupData group : groupDataList){
//                if(group.getKey().equals(availabilityGroupKey)){
//                    vpcGroupKeys.add(group.getGroup()); //vpc의 key를 얻는데 1개 밖에 오지 않지 당연히.
//                }
//            }
//        }
        String eni = "";
        String transit = "";
        System.out.println("Node link list: "+ nodeDataList);
        for (NodeData node : nodeDataList) {
            if (node.getKey().contains("VPC Elastic Network Interface")) {
                eni = node.getKey();
            }
            if (node.getKey().contains("Transit Gateway")) {
                transit = node.getKey();
            }
        }

        LinkData newLink = new LinkData();
        newLink.setFrom(transit);
        newLink.setTo(eni);
        linkDataList.add(newLink);

        return linkDataList;
    }

}