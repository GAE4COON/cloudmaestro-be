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
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Server", "EC2");
                nodeData.setKey(nodeKey);
                nodeData.setText("EC2");
                nodeData.setSource("/img/AWS_icon/Arch_Compute/Arch_Amazon-EC2_48.svg");
                nodeData.setType("Compute");
            } else if (node.contains("Anti DDoS")) {
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("Anti DDoS", "Shield");
                nodeData.setKey(nodeKey);
                nodeData.setText("Shield");
                nodeData.setSource("/img/AWS_icon/Arch_Security-Identity-Compliance/Arch_AWS-Shield_48.svg");
                nodeData.setType("Security-Identity-Compliance");
            } else if (node.contains("IPS")) {
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IPS", "CloudTrail");
                nodeData.setKey(nodeKey);
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");
            } else if (node.contains("IDS")){
                String nodeKey = nodeData.getKey();
                nodeKey = nodeKey.replace("IDS", "CloudTrail");
                nodeData.setKey(nodeKey);
                nodeData.setText("CloudTrail");
                nodeData.setSource("/img/AWS_icon/Arch_Management-Governance/Arch_AWS-CloudTrail_48.svg");
                nodeData.setType("Management-Governance");

            }
            else if (node.contains("Database")) {
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
            System.out.println("linkData" + linkData);
            String value;

            // server, web server
            if (node.contains("Server")) {
                value = node.replace("Server", "EC2");
                linkData.setFrom(value);
            } else if (node.contains("Anti DDoS")) {
                value = node.replace("Anti DDoS", "Shield");
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
//                groupData.setStroke("rgb(0,164,166)");
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
            if(nodeData.getKey().contains("CloudTrail")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CloudFront")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CodeDeploy")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("CloudWatch")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("Simple Storage Service")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("Internet")){
                nodeData.setGroup("Region");
            }
            else if(nodeData.getKey().contains("NACL")){
                nodeData.setGroup("VPC");
            }
       }
    }

    public void addNat(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
        // 모든 public subnet 그룹을 찾기
        int natCount = 1;

        List<String> publicSubnetGroupKeys = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Public subnet")) {
                publicSubnetGroupKeys.add(group.getKey());
            }
        }

        if(publicSubnetGroupKeys.isEmpty()){
            return;
        }
        //TODO 여기에 NACL+natCount의 loc를 찾아 해당 y축의 위치에 +를 엄청해주면 된다
        String newLoc = "";
        for (String publicSubnetGroupKey : publicSubnetGroupKeys) {

            for (NodeData nodeData : nodeDataList){
                if(nodeData.getKey().equals("NACL"+natCount)){
                    String location = nodeData.getLoc();
                    String[] locParts = location.split(" ");

                    double x = Double.parseDouble(locParts[0]);
                    double y = Double.parseDouble(locParts[1]);
                    newLoc = x + " " + (y-250);
                }
            }

            NodeData natNode = new NodeData();
            natNode.setKey("NAT"+natCount);
            natNode.setText("NAT");
            natNode.setLoc(newLoc);
            natNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_NAT-Gateway_48.svg");
            natNode.setType("Networking-Content-Delivery");
            natNode.setGroup(publicSubnetGroupKey);

            // NAT 노드를 리스트에 추가
            nodeDataList.add(natNode);
            natCount++;
        }

    }

    public void addNacl(List<NodeData> nodeDataList, List<GroupData> groupDataList){
        int naclCount = 1;


        List<String> privateSubnetGroupKeys = new ArrayList<>();
        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Private subnet")) {
                privateSubnetGroupKeys.add(group.getKey());
            }
        }
        if (privateSubnetGroupKeys.isEmpty()){
            return;
        }
        for (String privateSubnetGroupKey : privateSubnetGroupKeys) {
            double minY = Double.MAX_VALUE; //MAX보다 작은 Y를 찾으면
            double minX = Double.MAX_VALUE;

            for (NodeData node : nodeDataList) {
                for (GroupData groupData : groupDataList) { //group를 순회한다
                    if(groupData.getKey().equals(node.getGroup())){
                        if(groupData.getGroup()!= null && groupData.getGroup().equals(privateSubnetGroupKey)){
                            String location = node.getLoc();
                            String[] locParts = location.split(" ");

                            double x = Double.parseDouble(locParts[0]);
                            double y = Double.parseDouble(locParts[1]);
                            if (y < minY || (y == minY && x < minX)) { // y축이 -일때 위로 올라간다 x는 -일때 왼쪽이 맞음
                                minY = y;
                                minX = x;
                            }
                        }
                    }
                }
            }

            System.out.println("miny: "+minY);
            System.out.println("minx: "+minX);
            String newLoc = (minX) + " " + (minY-130);

            NodeData naclNode = new NodeData();
            naclNode.setKey("NACL"+naclCount); // NAT 키를 고유하게 만듦
            naclNode.setText("NACL");
            naclNode.setLoc(newLoc); // 계산된 위치 설정
            naclNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_Network-Access-Control-List_48.svg");
            naclNode.setType("Networking-Content-Delivery");
            naclNode.setGroup(privateSubnetGroupKey);

            // NAT 노드를 리스트에 추가
            nodeDataList.add(naclNode);
            naclCount++;
        }
    }
    public void addInternet(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        if(groupDataList.isEmpty()){
            return;
        }
        NodeData internetNode = new NodeData();
        internetNode.setKey("Internet");
        internetNode.setText("Internet");
        internetNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_Internet-Gateway_48.svg");
        internetNode.setType("Networking-Content-Delivery");
        internetNode.setGroup("VPC");
        internetNode.setLoc("330 -450");
        nodeDataList.add(internetNode);

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Public subnet")) {
                LinkData link = new LinkData();
                link.setFrom("Internet");
                link.setTo(group.getKey()); //여기에 public subnet이 와야함
                linkDataList.add(link);
            }
        }

        for (GroupData group : groupDataList) {
            if (group.getKey().contains("Public subnet")) {
                String privateSubnetKey = group.getKey().replace("Public", "Private");
                LinkData link = new LinkData();
                link.setFrom(group.getKey());
                link.setTo(privateSubnetKey);
                linkDataList.add(link);
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
        // 기본 옵션들 추가하기




    }

    public void addNetwork(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList){
        addNacl(nodeDataList, groupDataList);
        addNat(nodeDataList, groupDataList);
        addInternet(nodeDataList, groupDataList, linkDataList);
    }


}
