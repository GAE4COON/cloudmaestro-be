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
    public void changeNodeSource(List<NodeData> nodeDataList) {
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
        return;
    }

    @Override
    public void changeGroupSource(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
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
                    if(group.getGroup()==null) continue;
                    if (!group.getGroup().equals(key)) continue;
                    group.setGroup(key + " Virtual private cloud (VPC) " + count);
                }
            }
            count++;
        }

        Map<List<NodeData>, List<GroupData>> result = new HashMap<>();
        result.put(nodeDataList, groupDataList);

        return;
    }


    public void changeGroupSource2(List<NodeData> nodeDataList,List<GroupData> groupDataList) {
        Set<String> groupKey = new HashSet<>();

        for(GroupData groupData:groupDataList){
            String group = groupData.getKey();
            groupKey.add(group);
        }
        int count = 1;
        List<GroupData> newGroupDataList = new ArrayList<>();
        for(String key:groupKey){
            if(key.contains("Security Group")) continue;
            for(GroupData groupData:groupDataList) {
                if(!groupData.getKey().equals(key)) continue;
                // public subnet 생성
                GroupData publicSubnet = new GroupData();
                publicSubnet.setKey(key+" Public subnet " + count);
                publicSubnet.setGroup(key+" Public subnet " + count);
                publicSubnet.setStroke("rgb(122,161,22)");
                publicSubnet.setType("AWS_Groups");
                publicSubnet.setText(key+" Public subnet " + count);
                publicSubnet.setIsGroup(true);
                newGroupDataList.add(publicSubnet);

                //private subnet 생성
                groupData.setKey(key+" Private subnet " + count);
                groupData.setText(key+" Private subnet " + count);
                groupData.setStroke("rgb(0,164,166)");

                for(NodeData node: nodeDataList) {
                    if (!node.getGroup().equals(key)) continue;
                    node.setGroup(key + " Private subnet " + count);
                }
                for(GroupData group: groupDataList) {
                    if(group.getGroup()==null) continue;
                    if (!group.getGroup().equals(key)) continue;
                    group.setGroup(key + " Private subnet " + count);
                }
            }
            count++;
        }
        groupDataList.addAll(newGroupDataList);
        Map<List<NodeData>, List<GroupData>> result = new HashMap<>();
        result.put(nodeDataList, groupDataList);

        return;
    }
    @Override

    public void changeLinkSource(List<LinkData> linkDataList) {
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
        return;
    }


    public void changeRegionandVpc(List<GroupData> groupDataList) {


        Set<String> groupKey = new HashSet<>();

        for(GroupData groupData:groupDataList) {
            String group = groupData.getKey();
            groupKey.add(group);
        }

        // Temporary list to store new GroupData objects
        for(GroupData groupData:groupDataList) {
            if(groupData.getKey().contains("Private subnet") || groupData.getKey().contains("Public subnet") ){
                groupData.setStroke("rgb(0,164,166)");
                groupData.setIsGroup(true);
                groupData.setGroup("Availability Zone");
                groupData.setType("AWS_Groups");
            }

        }
        // Add all new GroupData objects to the original list
        GroupData availableSubnet = new GroupData();
        availableSubnet.setIsGroup(true);
        availableSubnet.setGroup("VPC");
        availableSubnet.setType("AWS_Groups");
        availableSubnet.setKey("Availability Zone");
        availableSubnet.setText("Availability Zone");
        availableSubnet.setStroke("rgb(0,164,166)");
        groupDataList.add(availableSubnet);

        GroupData vpcSubnet = new GroupData();
        vpcSubnet.setIsGroup(true);
        vpcSubnet.setGroup("Region");
        vpcSubnet.setType("AWS_Groups");
        vpcSubnet.setKey("VPC");
        vpcSubnet.setText("VPC");
        vpcSubnet.setStroke("rgb(140,79,255)");
        groupDataList.add(vpcSubnet);

        GroupData regionSubnet = new GroupData();
        regionSubnet.setIsGroup(true);
        regionSubnet.setType("AWS_Groups");
        regionSubnet.setKey("Region");
        regionSubnet.setText("Region");
        regionSubnet.setStroke("rgb(0,164,166)");
        groupDataList.add(regionSubnet);

    }

    public void moveNodeToRegion(List<NodeData> nodeDataList){
       for(NodeData nodeData : nodeDataList){
            if(nodeData.getKey().contains("Shield")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CloudTrail")){
                nodeData.setGroup("Region");
            }
       }
    }




    @Override
    public void changeAll(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        changeNodeSource(nodeDataList);
        changeLinkSource(linkDataList);
        changeGroupSource(nodeDataList, groupDataList);
    }

    public void changeAll2(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        changeNodeSource(nodeDataList);
        changeLinkSource(linkDataList);
        changeGroupSource2(nodeDataList, groupDataList);
    }


    public void setRegionAndVpcData(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        changeRegionandVpc(groupDataList);
        // Region에 옮기기
        moveNodeToRegion(nodeDataList);


    }


}
