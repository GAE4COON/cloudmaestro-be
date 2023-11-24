package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.*;

@Service
public class AvailableService {

    public int alb_index =0;
    public double alb_node_x; public double alb_node_y; public double node_x; public double node_y;
    public String originalpublicsubnetname = ""; public String originalprivatesubnetname = ""; public int Auto_index = 0;

    public void availalbeService(List<ZoneDTO> zoneRequirements, List<NodeData>nodeDataList,List<GroupData> groupDataList,List<LinkData>linkDataList) {


        // 위치 정보가 제일 높은 Y의 Node를 선택을 함
        NodeData highestNode = null;
        highestNode = findNodeWithHighestYCoordinate(nodeDataList);

        System.out.println("highestNode: " +highestNode);


        String location = highestNode.getLoc();
        // alb node 위치 설정
        String[] locParts = location.split(" ");
        alb_node_x = Double.parseDouble(locParts[0]);
        alb_node_y = Double.parseDouble(locParts[1]);
        // nat_node 위치 설정
        double nat_node_x = Double.parseDouble(locParts[0]);
        double nat_node_y = Double.parseDouble(locParts[1]);

        alb_node_x += 460; alb_node_y += 220;
        nat_node_x += 10; nat_node_y += 600;

        int key = -10;

        // AZ 존은 하나 생성
        GroupData Azdata = makeAz(groupDataList);

        // linkdata from, to 를 기준으로 zoneRequirement를 정렬시키기

        linkDataList.sort(Comparator.comparing(LinkData::getFrom).thenComparing(LinkData::getTo));


        for(int i = 0; i < zoneRequirements.size(); i++){
            String zone_name = zoneRequirements.get(i).getName();
            for(LinkData linkdata : linkDataList){
                if(linkdata.getFrom().contains(zone_name) && linkdata.getFrom().contains("Public")){
                    originalpublicsubnetname = linkdata.getFrom();
                }
                if(linkdata.getTo().contains(zone_name) && linkdata.getTo().contains("Private")){
                    originalprivatesubnetname = linkdata.getTo();
                }
            }


            String publicSubnetName = originalpublicsubnetname + 2;
            String privateSubnetName = originalprivatesubnetname + 2;

            // Availalbe Zone 생성
            NodeData publicSubnetNode = createNodeData(publicSubnetName, "AWS_Groups", "Availability Zone2", null, "rgb(122,161,22)",null);
            nodeDataList.add(publicSubnetNode);

            NodeData privateSubnetNode = createNodeData(privateSubnetName, "AWS_Groups", "Availability Zone2", null, "rgb(0,164,166)",null);
            nodeDataList.add(privateSubnetNode);

            // link 정보 연결하기
            LinkData pubToPriv = createLinkData(publicSubnetName, privateSubnetName, key - 1);
            linkDataList.add(pubToPriv);

            key -= 1;
            LinkData intToPub = createLinkData("Internet Gateway", publicSubnetName, key - 1);
            linkDataList.add(intToPub);

            key -= 1;
            NodeData natNode = makeNat(linkDataList,nodeDataList,publicSubnetName,nat_node_x, nat_node_y,i);
            nat_node_x += 10; nat_node_y += 400;
            // nat 기준으로 node data 설정
            node_x = nat_node_x + 430; node_y = nat_node_y - 460;

            // Available Node 정렬하기
            List<String> availableNodes = zoneRequirements.get(i).getAvailableNode();

            linkDataList.sort(Comparator.comparing(LinkData::getFrom).thenComparing(LinkData::getTo));
            List<String> availalbeNodes = LinkDataSort(linkDataList, availableNodes);

//            if((availalbeNodes.size()> 0) && (zoneRequirements.get(i).getServerNode().size() > 0)){
//                // 둘다 동시에 됐을 때 사고다 ,, 레알 ㅋ
//
//            }
            if (zoneRequirements.get(i).getServerNode().size() > 0) {

                ServerNode(zoneRequirements.get(i).getServerNode(), linkDataList,groupDataList,nodeDataList,node_x, node_y, key, privateSubnetName,originalprivatesubnetname);

            }

//            if (availalbeNodes.size()> 0) {
//                // ALB node 생성 및 node 연결
//                Available(linkDataList,groupDataList,nodeDataList,availableNodes, node_x, node_y, key, privateSubnetName);
//            }

        }



    }



    public List<String> LinkDataSort(List<LinkData> linkDataList, List<String> availableNodes) {

        Map<String, Integer> linkDataOrder = new HashMap<>();
        for (int i = 0; i < linkDataList.size(); i++) {
            String key = linkDataList.get(i).getFrom(); // or any relevant property
            linkDataOrder.put(key, i);
        };
        Comparator<String> sortByLinkDataOrder = (node1, node2) -> {
            Integer order1 = linkDataOrder.getOrDefault(node1, Integer.MAX_VALUE);
            Integer order2 = linkDataOrder.getOrDefault(node2, Integer.MAX_VALUE);
            return Integer.compare(order1, order2);
        };
        availableNodes.sort(sortByLinkDataOrder);
        return availableNodes;
    }

    public void ServerNode(List<String> serverNode, List<LinkData> linkDataList, List<GroupData> groupDataList, List<NodeData> nodeDataList, double node_x, double node_y, int key, String privateSubnetName, String originalprivatesubnetname) {

        int text_index;
        List<String> exceptNode2 = new ArrayList<>();  // 리스트 초기화
        for(String node : serverNode){
            List<NodeData> exceptNode = new ArrayList<>();
            // Node 1개만 빼고 제외하기
            if (node.contains("Security Group")){
                // 먼저 노드 하나만 빼고 제외하기
                ExceptNode(node, nodeDataList, groupDataList, exceptNode);
                text_index = Auto_index;
                // 기존에 있는 그룹을 Auto Group으로 변화시키기
                GroupData newAutoGroup = createAutoGroup(node, Auto_index, text_index,nodeDataList, groupDataList,originalprivatesubnetname);

                Auto_index += 1;

                // 일단 AutoGroup은 복사를 한다.
                GroupData CopyAutoGroup = createAutoGroup(node + "a" ,Auto_index, text_index, nodeDataList, groupDataList,privateSubnetName);
                System.out.println("CopyAutoGroup: "+ CopyAutoGroup);

                // NodeData 순환하기
                List<NodeData> newNodes = new ArrayList<>(); // 새로운 노드를 저장할 리스트

                for (NodeData nodedata : nodeDataList) {
                    if (nodedata.getGroup().equals(node)) {
                        System.out.println("Secruityg NodeData: " + nodedata);
                        NodeData copiedNode = new NodeData();
                        node_x += 200;
                        String newLoc = (node_x) + " " + (node_y);

                        copiedNode.setText(nodedata.getText());
                        copiedNode.setType(nodedata.getType());
                        copiedNode.setKey(nodedata.getKey() + "a");
                        copiedNode.setSource(nodedata.getSource());
                        copiedNode.setIsGroup(null);
                        copiedNode.setGroup(CopyAutoGroup.getKey());
                        copiedNode.setLoc(newLoc);

                        System.out.println("copiedNode: " + copiedNode);
                        newNodes.add(copiedNode); // 새 노드를 별도의 리스트에 추가
                    }
                }

                nodeDataList.addAll(newNodes); // 순회가 끝난 후 새 노드들을 nodeDataList에 추가

            }
            if(node.contains("EC2")){


                if (exceptNode2.isEmpty() && !exceptNode2.contains(node)) { // 리스트가 비어있지 않고, 특정 node가 포함되지 않은 경우
                    System.out.println("Except2Node" + exceptNode2);
                    System.out.println("Node" + node);

                    exceptNode2 = ExceptandAddInvidiualNode(nodeDataList, linkDataList, groupDataList, node);
                    // Available 에 Node 넣기
                    text_index = Auto_index;
                    addOriginalNode(exceptNode2, groupDataList, nodeDataList, originalprivatesubnetname, Auto_index, text_index);
                    Auto_index += 1;
                    addCopyNode(exceptNode2, groupDataList, nodeDataList, privateSubnetName, Auto_index, text_index);

                    removeLinkandNode(exceptNode2, linkDataList, nodeDataList);
                }
            }

            Auto_index += 1;
        }


        Auto_index += 1;

    }

    public void removeLinkandNode(List<String> exceptNode, List<LinkData> linkDataList, List<NodeData> nodeDataList) {
        List<NodeData> exceptNode2 = new ArrayList<>(); // 올바른 리스트 초기화
        for (int i = 0; i < exceptNode.size(); i++) {
            for (NodeData nodedata : nodeDataList) {
                if (nodedata.getKey().contains(exceptNode.get(i)) && nodedata.getGroup().contains("Auto Scaling group")) {
                    // Auto Scaling group에 속하는 노드는 제외
                } else if (nodedata.getKey().contains(exceptNode.get(i))) {
                    // Auto Scaling group에 속하지 않는 노드는 삭제 목록에 추가
                    exceptNode2.add(nodedata);
                }
            }
        }
        nodeDataList.removeAll(exceptNode2); // 삭제 목록에 있는 모든 노드 제거
    }


    public void addCopyNode(List<String> exceptNode, List<GroupData> groupDataList, List<NodeData> nodeDataList, String privateSubnetName, int autoIndex, int text_index) {
        NodeData AutoGroup = new NodeData();
        AutoGroup.setIsGroup(true);
        AutoGroup.setStroke("rgb(237,113,0)");
        AutoGroup.setText("Auto Scaling group" + text_index);
        AutoGroup.setKey("Auto Scaling group" + autoIndex);
        AutoGroup.setSource("/img/AWS_icon/AWS_Groups/Auto_Scaling_group.svg");
        AutoGroup.setType("AWS_Groups");
        AutoGroup.setGroup(privateSubnetName);
        nodeDataList.add(AutoGroup);

        List<NodeData> newNodes = new ArrayList<>();
        for(NodeData nodedata : nodeDataList){
            if(nodedata.getKey().equals(exceptNode.get(0))){
                NodeData copiedNode = new NodeData();
                node_x += 200;
                String newLoc = (node_x) + " " + (node_y);

                copiedNode.setText(nodedata.getText());
                copiedNode.setType(nodedata.getType());
                copiedNode.setKey(nodedata.getKey() + 2);
                copiedNode.setSource(nodedata.getSource());
                copiedNode.setIsGroup(null);
                copiedNode.setGroup("Auto Scaling group" + autoIndex);
                copiedNode.setLoc(newLoc);

                System.out.println("copiedNode: " + copiedNode);
                newNodes.add(copiedNode);

            }
        }

        nodeDataList.addAll(newNodes);


    }

    public void addOriginalNode(List<String> exceptNode, List<GroupData> groupDataList, List<NodeData> nodeDataList, String originalprivatesubnetname, int autoIndex, int text_index) {
        NodeData AutoGroup = new NodeData();
        for(NodeData nodedata : nodeDataList){
            if(nodedata.getKey().equals(exceptNode.get(0))){
                nodedata.setGroup("Auto Scaling group" + autoIndex);
                AutoGroup.setIsGroup(true);
                AutoGroup.setStroke("rgb(237,113,0)");
                AutoGroup.setText("Auto Scaling group" + text_index);
                AutoGroup.setKey("Auto Scaling group" + autoIndex);
                AutoGroup.setSource("/img/AWS_icon/AWS_Groups/Auto_Scaling_group.svg");
                AutoGroup.setType("AWS_Groups");
                AutoGroup.setGroup(originalprivatesubnetname);

            }
        }
        nodeDataList.add(AutoGroup);


    }

    public List<String> ExceptandAddInvidiualNode(List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, String node) {
        List<String> ExceptNode = new ArrayList<>();
        String to = "";
        int text_index;
        for(LinkData listdata : linkDataList){
            if(listdata.getFrom().equals(node)){
                to = listdata.getTo();
                System.out.println("TO: " + to);
            }

        }
        for(LinkData listdata2: linkDataList){
            if(listdata2.getTo().equals(to)){
                ExceptNode.add(listdata2.getFrom());
            }
        }


        System.out.println("ExceptNode: " + ExceptNode);
        return ExceptNode;
    }

    public GroupData createAutoGroup(String security_group, int index, int text_index, List<NodeData> nodeDataList, List<GroupData> groupDataList, String privateSubnetName) {
        System.out.println("SecurityGroup: " + security_group);
        GroupData autogroup = new GroupData();
        autogroup.setKey(security_group);
        autogroup.setText(security_group);
        autogroup.setIsGroup(true);
        autogroup.setGroup("Auto Scaling group" + index);
        autogroup.setType("group");
        autogroup.setStroke("rgb(221,52,76)");

        groupDataList.add(autogroup);

        NodeData AutoGroup = new NodeData();
        AutoGroup.setIsGroup(true);
        AutoGroup.setStroke("rgb(237,113,0)");
        AutoGroup.setText("Auto Scaling group" + text_index);
        AutoGroup.setKey("Auto Scaling group" + index);
        AutoGroup.setSource("/img/AWS_icon/AWS_Groups/Auto_Scaling_group.svg");
        AutoGroup.setType("AWS_Groups");
        AutoGroup.setGroup(privateSubnetName);

        nodeDataList.add(AutoGroup);
        return autogroup;
    }

    public void ExceptNode(String node, List<NodeData> nodeDataList, List<GroupData> groupDataList, List<NodeData> exceptNode) {
        for(NodeData nodedata : nodeDataList){
            if(nodedata.getGroup().equals(node)){
                exceptNode.add(nodedata);
            }
        }
        System.out.println("NodeName : " + node);
        System.out.println("ExceptNode : " + exceptNode);

        if(exceptNode.size() >=2){
            for (int i = 1; i < exceptNode.size(); i++) {
                nodeDataList.remove(exceptNode.get(i));
            }
        }
        List<GroupData> exceptgroup = new ArrayList<>();
        for(GroupData groupData : groupDataList){
            if(groupData.getKey().equals(node)){
                exceptgroup.add(groupData);
            }
        }

        groupDataList.remove(exceptgroup.get(0));

    }


    public void Available(List<LinkData> linkDataList, List<GroupData> groupDataList, List<NodeData> nodeDataList, List<String> availableNode,double node_x, double node_y, int key, String privateSubnetName){
        for(String node : availableNode)
        {
            // NodeData 복사 시작
            List<NodeData> node_temp_list = new ArrayList<>();
            String security_group = "";

            // ALB Node 생성
            NodeData AlbNode;
            AlbNode = makeALb(alb_index,alb_node_x,alb_node_y);

            // internet gateway to ALB
            LinkData addIntoALB = createLinkData("Internet Gateway", AlbNode.getKey(), key - 1);
            linkDataList.add(addIntoALB);

            // ALB to security Group
            LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
            linkDataList.add(addALBintoGroup);

            // node 추가하기
            if (node.contains("Security Group")){
                security_group = node + 2;
                GroupData new_security_group = createAndConfigureGroupData(security_group, privateSubnetName);
                // 새로운 그룹 생성하고 그룹과 alb의 연결
                addSecurityGroup(node,security_group,new_security_group,groupDataList,nodeDataList,linkDataList,node_temp_list,node_x,node_y,AlbNode, key-=1);

            }

            else if(!node.contains("Security Group")){
                // 새로운 인스턴스 생성하고, alb과 노드들과의 연결
                double[] newCoordinates = addNode(node,groupDataList,nodeDataList,linkDataList,AlbNode,node_x,node_y,privateSubnetName, key-=1);
                node_x = newCoordinates[0];
                node_y = newCoordinates[1];
            }


            nodeDataList.add(AlbNode);
            alb_index +=1 ;
            alb_node_x += 220;
            alb_node_y += 10;

        }


    }

    public double[] addNode(String node, List<GroupData> groupDataList, List<NodeData> nodeDataList, List<LinkData> linkDataList, NodeData AlbNode, double node_x, double node_y, String privateSubnetName, int Key) {
        String node_name = node + "2";
        NodeData nodedata = new NodeData();
        node_x += 150;
        nodedata.setGroup(privateSubnetName);
        nodedata.setText("EC2");
        nodedata.setType("Compute");
        nodedata.setKey(node_name);
        nodedata.setSource( "/img/AWS_icon/Arch_Compute/Arch_Amazon-EC2_48.svg");
        String newLoc = (node_x) + " " + (node_y);
        nodedata.setLoc(newLoc);
        nodedata.setIsGroup(null);

        nodeDataList.add(nodedata);
        LinkData albtogroup = createLinkData(AlbNode.getKey(),nodedata.getKey(),Key-=1);
        System.out.println("albtogroup: " + albtogroup);
        linkDataList.add(albtogroup);
        return new double[]{node_x, node_y};
    }

    public double[] addSecurityGroup(String node, String security_group, GroupData new_security_group, List<GroupData> groupDataList, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<NodeData> node_temp_list, double node_x, double node_y, NodeData AlbNode, int key) {
        if (!groupDataList.contains(new_security_group)) {
            groupDataList.add(new_security_group); // Add new security group if not present
        }
        for (NodeData nodedata : nodeDataList) {
            if (nodedata.getGroup().equals(node)) {
                node_temp_list.add(nodedata);
            }
        }
        // 새로운 그룹을 생성하면서, 그룹에 해당하는 노드들  복사
        for (NodeData copy_node : node_temp_list){
            node_x += 150;
            String newLoc = (node_x) + " " + (node_y);
            NodeData copiedNode = new NodeData();
            copiedNode.setText(copy_node.getText());
            copiedNode.setType(copy_node.getType());
            copiedNode.setKey(copy_node.getKey() + alb_index);
            copiedNode.setSource(copy_node.getSource());
            copiedNode.setIsGroup(null);
            copiedNode.setGroup(security_group);
            copiedNode.setLoc(newLoc);
            nodeDataList.add(copiedNode);
        }
        System.out.println("security_group: "+new_security_group);
        // ALB Node와 연결
        LinkData albtogroup = createLinkData(AlbNode.getKey(),new_security_group.getKey(),key -=1);
        System.out.println("albtogroup: " + albtogroup);
        linkDataList.add(albtogroup);
        return new double[]{node_x, node_y};

    }

    public GroupData makeAz(List<GroupData> groupDataList) {
        GroupData Azdata = new GroupData();
        Azdata.setKey("Availability Zone2");
        Azdata.setText("Availability Zone");
        Azdata.setIsGroup(true);
        Azdata.setGroup("VPC");
        Azdata.setType("AWS_Groups");
        Azdata.setStroke("rgb(0,164,166)");

        groupDataList.add(Azdata);
        return Azdata;
    }

    public NodeData makeNat(List<LinkData> linkDataList, List<NodeData> nodeDataList, String publicSubnetName, double natNodeX, double natNodeY, int index) {
        int nat_sum = 1;
        for(LinkData linkdata : linkDataList){
            if(linkdata.getFrom().contains("Public Subnet"))
            {
                nat_sum +=1 ;
            }
        }

        nat_sum += index;
        NodeData natnode = new NodeData();
        natnode.setText("NAT Gateway");
        natnode.setType("Networking-Content-Delivery");
        natnode.setKey("NAT Gateway" + nat_sum);
        natnode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_NAT-Gateway_48.svg");
        String newLoc = (natNodeX) + " " + (natNodeY);
        natnode.setLoc(newLoc);
        natnode.setIsGroup(null);
        natnode.setGroup(publicSubnetName);

        nodeDataList.add(natnode);
        return natnode;


    }

    public NodeData findNodeWithHighestYCoordinate(List<NodeData> nodeDataList) {
        NodeData highestYNode = null;
        double highestY = Double.NEGATIVE_INFINITY;

        for (NodeData nodedata : nodeDataList) {

            if(nodedata.getKey().contains("NAT")){
                String location = nodedata.getLoc();
                String[] locParts = location.split(" ");

                double y = Double.parseDouble(locParts[1]);
                if (y > highestY) {
                    highestY = y;
                    highestYNode = nodedata;
                }
            }
        }

        return highestYNode;
    }

    public NodeData makeALb(int index, double node_x, double node_y){
        NodeData AlbNode = new NodeData();
        AlbNode.setText("Application Load Balancer (ALB)");
        AlbNode.setKey("Application Load Balancer (ALB)" + index);
        AlbNode.setFigure("Rectangle");
        AlbNode.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
        AlbNode.setType("Networking-Content-Delivery");
        String newLoc = (node_x) + " " + (node_y);
        AlbNode.setLoc(newLoc);
        AlbNode.setGroup("VPC");
        System.out.println("ALBNode: "+AlbNode);

        return AlbNode;

    }

    public NodeData createNodeData(String name, String type, String group, String source, String stroke, String newLoc) {
        NodeData node = new NodeData();
        node.setKey(name);
        node.setText(name);
        node.setIsGroup(true);
        node.setGroup(group);
        node.setSource(source);
        node.setType(type);
        node.setStroke(stroke);
        if (newLoc != null && !newLoc.isEmpty()) {
            node.setLoc(newLoc);
        }
        return node;
    }
    public LinkData createLinkData(String from, String to, int key) {
        LinkData link = new LinkData();
        link.setFrom(from);
        link.setTo(to);
        link.setKey(key);
        return link;
    }
    public GroupData createAndConfigureGroupData(String securityGroup, String privateSubnetName) {
        GroupData group = new GroupData();
        group.setKey(securityGroup);
        group.setText("Security Group");
        group.setIsGroup(true);
        group.setGroup(privateSubnetName);
        group.setType("group");
        group.setStroke("rgb(221,52,76)");

        return group;
    }



}