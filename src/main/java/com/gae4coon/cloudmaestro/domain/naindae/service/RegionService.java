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
        Map<String, Object> result = new HashMap<>();
        List<NodeData> newNodeDataList = new ArrayList<>();
        List<GroupData> newGroupDataList = new ArrayList<>();
        List<LinkData> newLinkDataList = new ArrayList<>();

        newNodeDataList = modifyNodeDataForNewRegion(nodeDataList);
        newGroupDataList = modifyGroupDataForNewRegion(groupDataList);
        newLinkDataList = modifyLinkDataForNewRegion(linkDataList);

        result.put("nodes", newNodeDataList);
        result.put("groups", newGroupDataList);
        result.put("links", newLinkDataList);

        return result;
    }

    public List<NodeData> modifyNodeDataForNewRegion(List<NodeData> originalNodeDataList) {
        List<NodeData> modifiedList = new ArrayList<>();        //
        for (NodeData node : originalNodeDataList) {
            NodeData newNode = new NodeData();
            newNode.setText(node.getText());
            newNode.setLoc(node.getLoc());
            newNode.setType(node.getType());
            newNode.setSource(node.getSource());
            newNode.setKey(node.getKey() + "2");
            newNode.setGroup(node.getGroup() + "2");
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
            newGroup.setKey(group.getKey() + "2");
            newGroup.setGroup(group.getGroup() + "2");
            newGroup.setStroke(group.getStroke());
            modifiedList.add(newGroup);
        }
        return modifiedList;
    }

    // LinkData 객체를 새로운 리전의 노드 및 그룹 키에 맞게 수정하는 메소드
    public List<LinkData> modifyLinkDataForNewRegion(List<LinkData> originalLinkDataList) {
        List<LinkData> modifiedList = new ArrayList<>();
        for (LinkData link : originalLinkDataList) {
            LinkData newLink = new LinkData();
            newLink.setFrom(link.getFrom() + "2");
            newLink.setTo(link.getTo() + "2");
            modifiedList.add(newLink);
        }
        return modifiedList;
    }
}
