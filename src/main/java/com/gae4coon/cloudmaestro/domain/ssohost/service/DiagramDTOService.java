package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiagramDTOService {
    
    public Map<String, Object> dtoGenerator(GraphLinksModel graphLinksModel){
        List<NodeData> dataArray = graphLinksModel.getNodeDataArray();
        List<NodeData> nodeDataList = new ArrayList<>();
        List<GroupData> groupDataList = new ArrayList<>();

        List<LinkData> linkDataList = graphLinksModel.getLinkDataArray();

        for (NodeData data : dataArray) {
            if (data.getIsGroup() != null) {
                GroupData groupData = new GroupData(data.getKey(), data.getText(), data.getIsGroup(), data.getGroup(), data.getType(), data.getStroke());
                groupDataList.add(groupData);
            } else {
                nodeDataList.add(data);
            }
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("nodeDataArray", nodeDataList);
        responseBody.put("groupDataArray", groupDataList);
        responseBody.put("linkDataArray", linkDataList);

        return responseBody;
    }

    
    public HashMap<String, Object> dtoComplete(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){

        List<Object> finalDataArray = new ArrayList<>();
        finalDataArray.addAll(nodeDataList);
        finalDataArray.addAll(groupDataList);

        finalDataArray.removeIf(Objects::isNull);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("class", "GraphLinksModel");
        responseBody.put("linkKeyProperty", "key");
        responseBody.put("nodeDataArray", finalDataArray);
        responseBody.put("linkDataArray", linkDataList);

        HashMap<String, Object> response = new HashMap<>();

        response.put("result", responseBody);

        return response;
    }


    public void addServiceGroup(List<GroupData> groupDataList){
        if(isGroupDataContains(groupDataList, "Service")) return;
        GroupData groupData = new GroupData();
        groupData.setKey("Service");
        groupData.setText("Service");
        groupData.setStroke("rgb(158, 224, 255)");
        groupDataList.add(groupData);
    }


    public boolean isNodeDataContains(List<NodeData> nodeDataList,String nodeText){
        for (NodeData node : nodeDataList) {
            if(node.getKey().contains(nodeText)) return true;
        }
        return false;
    }

    public boolean isGroupDataContains(List<GroupData> groupDataList, String groupText){
        for (GroupData group: groupDataList){
            if(group.getKey().contains(groupText)) return true;
        }
        return false;
    }

    public LinkData getLinkDataByTo(List<LinkData> linkDataList, String from){
        for (LinkData link : linkDataList) {
            if(link.getFrom().equals(from)) return link;
        }
        return null;
    }

    public NodeData getNodeDataByKey(List<NodeData> nodeDataList, String key){
        for (NodeData node : nodeDataList) {
            if(node.getKey().equals(key)) return node;
        }
        return null;
    }

    public GroupData getGroupDataByKey(List<GroupData> groupDataList, String key){
        for (GroupData group : groupDataList) {
            if(group.getKey().equals(key)) return group;
        }
        return null;
    }

    public List<NodeData> getNodeListByText(List<NodeData> nodeDataList, String text){
        List<NodeData> nodeList = new ArrayList<>();
        for (NodeData node : nodeDataList) {
            if(node.getKey().contains(text)) nodeList.add(node);
        }
        return nodeList;
    }

    public List<GroupData> getGroupListByText(List<GroupData> groupDataList, String text) {
        List<GroupData> groupList = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getText().contains(text)) groupList.add(group);
        }
        return groupList;
    }
}
