package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    List<NodeData> nodeDataList;
    List<GroupData> groupDataList;
    List<LinkData> linkDataList;

    public DiagramDTOService(Map<String, Object> responseArray){
        nodeDataList = (List<NodeData>) responseArray.get("nodeDataArray");
        groupDataList = (List<GroupData>) responseArray.get("groupDataArray");
        linkDataList = (List<LinkData>) responseArray.get("linkDataArray");
    }

    public void createServiceGroup(){
        if(isGroupDataContains("Service")) return;

        GroupData groupData = new GroupData();
        groupData.setKey("Service");
        groupData.setText("Service");
        groupData.setStroke("rgb(158, 224, 255)");
        groupDataList.add(groupData);
    }

    
    public boolean isNodeDataContains(String nodeText){
        for (NodeData node : nodeDataList) {
            if(node.getKey().contains(nodeText)) return true;
        }
        return false;
    }

    
    public boolean isGroupDataContains(String groupText){
        for (GroupData group: groupDataList){
            if(group.getKey().contains(groupText)) return true;
        }
        return false;
    }
    
    public LinkData getLinkDataByTo(String from){
        for (LinkData link : linkDataList) {
            if(link.getFrom().equals(from)) return link;
        }
        return null;
    }

    public NodeData getNodeDataByKey(String key){
        for (NodeData node : nodeDataList) {
            if(node.getKey().equals(key)) return node;
        }
        return null;
    }

    public GroupData getGroupDataByKey(String key){
        for (GroupData group : groupDataList) {
            if(group.getKey().equals(key)) return group;
        }
        return null;
    }

    public List<NodeData> getNodeListByText(String text){
        List<NodeData> nodeList = new ArrayList<>();
        for (NodeData node : nodeDataList) {
            if(node.getKey().contains(text)) nodeList.add(node);
        }
        return nodeList;
    }

    public List<GroupData> getGroupListByText(String text){
        List<GroupData> groupList = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if(group.getText().contains(text)) groupList.add(group);
        }
        return groupList;
    }
}
