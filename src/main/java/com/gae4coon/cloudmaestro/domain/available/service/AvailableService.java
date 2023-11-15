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

        // PROD 개발망에 들어온다고 가정을 한다.

        NodeData albNode = new NodeData();
        albNode.setKey("Elastic Load Balancing Application Load Balancer" + albCount);
        albNode.setText("Elastic Load Balancing Application Load Balancer");
        albNode.setType("Networking-Content-Delivery");
        albNode.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
        albNode.setType("Networking-Content-Delivery");
        albNode.setFigure("Rectangle");
        albNode.setGroup("Availability Zone");


        nodeDataList.add(albNode);
        // Public subnet - ALB 연결
        List<LinkData> tempLinkDataList = new ArrayList<>();
        List<LinkData> itemsToRemove = new ArrayList<>();
        for(LinkData linkdata : linkDataList){
            if(linkdata.getFrom().contains(name.concat("Public subnet"))){
                LinkData data = new LinkData();
                data.setFrom(linkdata.getFrom());
                data.setTo(albNode.getKey());
                data.setKey(linkdata.getKey() - 1);
                tempLinkDataList.add(data);
            }
            if(linkdata.getFrom().contains(name.concat("Public subnet"))){
                itemsToRemove.add(linkdata);
            }
        }
        linkDataList.removeAll(itemsToRemove);
        linkDataList.addAll(tempLinkDataList);

        // Secruity Group 에 ec2가 포함되어 있는 지 && 해당 망의 group 인지
        List<String> includeEc2Security = new ArrayList<>();
        for(NodeData nodedata : nodeDataList){
            // Security Group에 ec2가 포함되어 있는지
            if(nodedata.getText().contains("EC2") && nodedata.getGroup().contains("Security Group")){
                String groupName = nodedata.getGroup();
                // 해당 망의 group data 인지
                for (GroupData group : groupDataList){
                    if(     group.getGroup() != null &&
                            group.getGroup().contains(name) &&
                            group.getKey().contains(groupName)){
                        includeEc2Security.add(nodedata.getGroup());
                    }
                }
            }
            //  해당 망에 security group이 없는 ec2가 있는 지
            if(nodedata.getText().contains("EC2") && !nodedata.getGroup().contains("Security Group")){
                includeEc2Security.add(nodedata.getKey());
            }

        }

        System.out.println("includeEc2" + includeEc2Security);

        // link data와 Ec2 or security group 간의 연결
        Set<String> setIncludeEc2 = new HashSet<>(includeEc2Security);

        for(String ec2 : setIncludeEc2) {

            System.out.println("ALBNode :  "+ albNode.getKey());
            LinkData data = new LinkData();
            data.setFrom(albNode.getKey());
            data.setTo(ec2);
            data.setKey(-1);
            tempLinkDataList.add(data);
        }
        linkDataList.addAll(tempLinkDataList);

        albCount += 1;

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
