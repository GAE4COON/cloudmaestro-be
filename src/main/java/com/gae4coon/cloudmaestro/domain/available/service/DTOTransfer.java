package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DTOTransfer {
    public void converMapToData(List<GroupData> groupDataList, List<NodeData> nodeDataList, List<LinkData> linkDataList,List<Map<String, Object>> linkDataArray, List<Map<String, Object>> nodeDataArray) {

        for(Map<String,Object> data : linkDataArray){
            LinkData linkdata = converMapToLinkData(data);
            System.out.println("LinkData : " + linkdata);
            linkDataList.add(linkdata);
        }


        // Group Data와 Node Data 의 변환
        for( Map<String, Object> data : nodeDataArray){
            if(data.containsKey("loc")){
                NodeData nodeData = convertMapToNodeData(data);
                nodeDataList.add(nodeData);
            }else{
                GroupData groupData = convertMapToGroupData(data);
                groupDataList.add(groupData);
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

    public LinkData converMapToLinkData(Map<String,Object> data) {
        LinkData linkdata = new LinkData();
        linkdata.setFrom((String) data.get("from"));
        linkdata.setKey((int)data.get("key"));
        linkdata.setTo((String) data.get("to"));
        return linkdata;

    }
}
