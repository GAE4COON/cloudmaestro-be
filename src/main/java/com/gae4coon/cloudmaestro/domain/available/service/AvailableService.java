package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvailableService {
    List<NodeData> nodeDataList; List<GroupData> groupDataList; List<LinkData> linkDataList;
    public int alb_index =0;
    public double alb_node_x; public double alb_node_y;

    public HashMap<String, Object> availalbeService(List<ZoneDTO> zoneRequirements, List<NodeData>nodeDataList,List<GroupData> groupDataList,List<LinkData>linkDataList) {

        HashMap<String, Object> result = new HashMap<>();

        // 위치 정보가 제일 높은 Y의 Node를 선택을 함
        NodeData highestNode = null;
        highestNode = findNodeWithHighestYCoordinate(nodeDataList);

        System.out.println("nodeDataList: " + nodeDataList);


        String location = highestNode.getLoc();
        // alb node 위치 설정
        String[] locParts = location.split(" ");
        alb_node_x = Double.parseDouble(locParts[0]);
        alb_node_y = Double.parseDouble(locParts[1]);
        // nat_node 위치 설정
        double nat_node_x = Double.parseDouble(locParts[0]);
        double nat_node_y = Double.parseDouble(locParts[1]);
        // node 위치 설정
        double node_x; double node_y;

        alb_node_x += 460; alb_node_y += 220;
        nat_node_x += 10; nat_node_y += 600;

        int key = -10;

        // AZ 존은 하나 생성
        GroupData Azdata = makeAz(groupDataList);

        // linkdata from, to 를 기준으로 zoneRequirement를 정렬시키기

        linkDataList.sort(Comparator.comparing(LinkData::getFrom).thenComparing(LinkData::getTo));

        for(int i = 0; i < zoneRequirements.size(); i++){
            String zone_name = zoneRequirements.get(i).getName();
            String publicsubnetname = "";
            String privatesubnetname = "";
            for(LinkData linkdata : linkDataList){
                if(linkdata.getFrom().contains(zone_name) && linkdata.getFrom().contains("Public")){
                    publicsubnetname = linkdata.getFrom();
                }
                if(linkdata.getTo().contains(zone_name) && linkdata.getTo().contains("Private")){
                    privatesubnetname = linkdata.getTo();
                }
            }


            String publicSubnetName = publicsubnetname + 2;
            String privateSubnetName = privatesubnetname + 2;

            // Availalbe Zone 생성
            NodeData publicSubnetNode = createNodeData(publicSubnetName, "AWS_Groups", "Availability Zone2", null, "rgb(122,161,22)",null);
            nodeDataList.add(publicSubnetNode);

            NodeData privateSubnetNode = createNodeData(privateSubnetName, "AWS_Groups", "Availability Zone2", null, "rgb(0,164,166)",null);
            nodeDataList.add(privateSubnetNode);

            // link 정보 연결하기
            LinkData pubToPriv = createLinkData(publicSubnetName, privateSubnetName, key - 1);
            linkDataList.add(pubToPriv);

            key -= 1;
            LinkData intToPub = createLinkData("VPC Internet Gateway", publicSubnetName, key - 1);
            linkDataList.add(intToPub);

            key -= 1;
            NodeData natNode = makeNat(linkDataList,nodeDataList,publicSubnetName,nat_node_x, nat_node_y,i);
            nat_node_x += 10; nat_node_y += 400;
            node_x = nat_node_x + 430; node_y = nat_node_y - 460;

            // Available Node 정렬하기
            List<String> availableNodes = zoneRequirements.get(i).getAvailableNode();

            linkDataList.sort(Comparator.comparing(LinkData::getFrom).thenComparing(LinkData::getTo));
            List<String> availalbeNodes = LinkDataSort(linkDataList, availableNodes);

            if (availalbeNodes.size()> 0) {
                // ALB node 생성 및 node 연결
                Available(availableNodes, node_x, node_y, key, privateSubnetName);
            }
            if (zoneRequirements.get(i).getServerNode().size() > 0) {
                ServerNode(zoneRequirements.get(i).getServerNode());
            }
        }


        //// ========================== Final =====================================
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
        //HashMap<String, Object> response = diagramDTOService.dtoComplete(nodeDataList, groupDataList, linkDataList);
        System.out.println("response"+ response);

        return response;

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

    private void ServerNode(List<String> serverNode) {

    }


    public void Available(List<String> availableNode,double node_x, double node_y, int key, String privateSubnetName){
        for(String node : availableNode)
        {
            // NodeData 복사 시작
            List<NodeData> node_temp_list = new ArrayList<>();
            String security_group = "";

            // ALB Node 생성
            NodeData AlbNode;
            AlbNode = makeALb(alb_index,alb_node_x,alb_node_y);

            // internet gateway to ALB
            LinkData addIntoALB = createLinkData("VPC Internet Gateway", AlbNode.getKey(), key - 1);
            linkDataList.add(addIntoALB);
            // ALB to security Group
            LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
            linkDataList.add(addALBintoGroup);

            // node 추가하기
            if (node.contains("Security Group")){
                security_group = node + 2;
                GroupData new_security_group = createAndConfigureGroupData(security_group, privateSubnetName);
                if (!groupDataList.contains(new_security_group)) {
                    groupDataList.add(new_security_group); // Add new security group if not present
                }
                for (NodeData nodedata : nodeDataList) {
                    if (nodedata.getGroup().equals(node)) {
                        node_temp_list.add(nodedata);
                    }
                }

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
                LinkData albtogroup = createLinkData(AlbNode.getKey(),new_security_group.getKey(),key -=1);
                System.out.println("albtogroup: " + albtogroup);
                linkDataList.add(albtogroup);
//            }else{
//                for (NodeData nodedata : nodeDataList) {
//                    if (nodedata.getKey().equals(node)) {
//                        node_x += 150;
//                        String newLoc = (node_x) + " " + (node_y);
//                        NodeData copiedNode = new NodeData();
//                        copiedNode.setText(nodedata.getText());
//                        copiedNode.setType(nodedata.getType());
//                        copiedNode.setKey(nodedata.getKey() + alb_index);
//                        copiedNode.setSource(nodedata.getSource());
//                        copiedNode.setIsGroup(null);
//                        copiedNode.setGroup(privateSubnetName);
//                        copiedNode.setLoc(newLoc);
//                        nodeDataList.add(copiedNode);
//                    }
//                }
            }

            nodeDataList.add(AlbNode);
            alb_index +=1 ;
            alb_node_x += 220;
            alb_node_y += 10;

        }



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
        natnode.setText("NAT");
        natnode.setType("Networking-Content-Delivery");
        natnode.setKey("NAT" + nat_sum);
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
            String location = nodedata.getLoc();
            String[] locParts = location.split(" ");

            double y = Double.parseDouble(locParts[1]);

            if (y > highestY) {
                highestY = y;
                highestYNode = nodedata;
            }
        }

        return highestYNode;
    }

    public NodeData makeALb(int index, double node_x, double node_y){
        NodeData AlbNode = new NodeData();
        AlbNode.setText("Application Load Balancer(ALB)");
        AlbNode.setKey("Application Load Balancer(ALB)" + index);
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
