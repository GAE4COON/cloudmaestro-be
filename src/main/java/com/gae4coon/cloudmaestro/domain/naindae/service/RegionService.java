package com.gae4coon.cloudmaestro.domain.naindae.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import io.swagger.v3.oas.models.links.Link;
import org.springframework.stereotype.Service;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionService {    //여기서 이미 dnsmulti에서 리전 하나를 생성한지 검사
    public void getRegion(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList) {
        List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();
        if(globalRequirements.contains("글로벌네트워크 구축 (멀티리전)") || globalRequirements.contains("리소스 이중화")) {
            long regionCount = groupDataList.stream()
                    .filter(group -> group.getKey().contains("Region"))
                    .count();
            String num = "1";
            if (regionCount >= 2){
                nodeDataList = addNode(nodeDataList,groupDataList,linkDataList,num);
                linkDataList = regionLink2(nodeDataList,linkDataList);
            }
            else{
                Map<String, Object> result = new HashMap<>();
                List<NodeData> newNodeDataList = new ArrayList<>();
                List<GroupData> newGroupDataList = new ArrayList<>();
                List<LinkData> newLinkDataList = new ArrayList<>();
                newNodeDataList = modifyNodeDataForNewRegion(nodeDataList);
                newGroupDataList = modifyGroupDataForNewRegion(groupDataList);
                newLinkDataList = modifyLinkDataForNewRegion(linkDataList);
                newNodeDataList = addNode(newNodeDataList, newGroupDataList, newLinkDataList, num);
                newLinkDataList = addLink(newNodeDataList, newGroupDataList, newLinkDataList, num);

                result.put("nodes", newNodeDataList);
                result.put("groups", newGroupDataList);
                result.put("links", newLinkDataList);

                Map<String, Object> originalResult = new HashMap<>();
                originalResult = getOriginalRegion(nodeDataList, groupDataList, linkDataList);
                nodeDataList = (List<NodeData>) originalResult.get("nodes");
                linkDataList = (List<LinkData>) originalResult.get("links");
                groupDataList = (List<GroupData>) originalResult.get("groups");

                linkDataList = regionLink(nodeDataList, newNodeDataList, linkDataList);
                nodeDataList.addAll(newNodeDataList);
                linkDataList.addAll(newLinkDataList);
                groupDataList.addAll(newGroupDataList);
            }

        }else {
            System.out.println();
        }
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

    public List<LinkData> regionLink2(List<NodeData> nodeDataList, List<LinkData> linkDataList) {
        String transit1 = "";
        String transit2 = "";
        int count = 0;
        for(NodeData nodeData : nodeDataList) {
            if (nodeData.getKey().contains("Transit Gateway") && count > 0) {
                transit2 = nodeData.getKey();
            }
            else if (nodeData.getKey().contains("Transit Gateway") && count == 0) {
                transit1 = nodeData.getKey();
                count++;
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
            if (node.getKey().contains("Route 53")){
                continue;
            }
            NodeData newNode = new NodeData();
            newNode.setText(node.getText());
            try {
                String location = node.getLoc();
                if (location == null || location.isEmpty()) {
                    throw new IllegalArgumentException("Location is null or empty for node: " + node);
                }

                String[] locParts = location.split(" ");
                if (locParts.length != 2) {
                    throw new IllegalArgumentException("Location format is incorrect for node: " + node);
                }
                double x = Double.parseDouble(locParts[0]);
                double y = Double.parseDouble(locParts[1]) + 1300;
                newNode.setLoc(x + " " + y); // 수정된 좌표 설정
                newNode.setType(node.getType());
                newNode.setSource(node.getSource());
                newNode.setKey("MR-"+node.getKey());
                newNode.setGroup("MR-"+node.getGroup());
                modifiedList.add(newNode);
            }
            catch (IllegalArgumentException ignored) {
            }

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
                NodeData attachment = new NodeData();
                attachment.setKey("Elastic Network Interface"+num);
                attachment.setText("Elastic Network Interface");
                attachment.setLoc("loc");
                attachment.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Amazon-VPC_Elastic-Network-Interface_48.svg");
                attachment.setType("Networking-Content-Delivery");
                attachment.setGroup(group.getKey());
                nodeDataList.add(attachment);
                attachmentCount++;
            }
        }

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Region")) {
                NodeData transit = new NodeData();
                transit.setKey("Transit Gateway"+num);
                transit.setText("Transit Gateway");
                transit.setLoc("loc");
                transit.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_AWS-Transit-Gateway_48.svg");
                transit.setType("Networking-Content-Delivery");
                transit.setGroup(group.getKey());
                nodeDataList.add(transit);
                transitCount++;
                num="2";
            }
        }

            return nodeDataList;
    }

    public List<LinkData> addLink(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList, String num) {
        List<String> availabilityGroupKeys = new ArrayList<>();
        List<String> vpcGroupKeys = new ArrayList<>();

        String eni = "";
        String transit = "";
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