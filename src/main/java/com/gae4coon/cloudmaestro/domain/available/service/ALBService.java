//package com.gae4coon.cloudmaestro.domain.available.service;
//
//import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
//import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
//import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
//
//import java.util.*;
//
//public class ALBService {
//    public AvailableService availableService;
//    public List<String> LinkDataSort(List<LinkData> linkDataList, List<String> availableNodes) {
//
//        Map<String, Integer> linkDataOrder = new HashMap<>();
//        for (int i = 0; i < linkDataList.size(); i++) {
//            String key = linkDataList.get(i).getFrom(); // or any relevant property
//            linkDataOrder.put(key, i);
//        };
//        Comparator<String> sortByLinkDataOrder = (node1, node2) -> {
//            Integer order1 = linkDataOrder.getOrDefault(node1, Integer.MAX_VALUE);
//            Integer order2 = linkDataOrder.getOrDefault(node2, Integer.MAX_VALUE);
//            return Integer.compare(order1, order2);
//        };
//        availableNodes.sort(sortByLinkDataOrder);
//        return availableNodes;
//    }
//
//    public void ServerNode(List<String> serverNode) {
//
//    }
//
//    public void Available(List<String> availableNode, double alb_node_x, double alb_node_y, double node_x, double node_y, int key, String privateSubnetName){
//        for(String node : availableNode)
//        {
//            // NodeData 복사 시작
//            List<NodeData> node_temp_list = new ArrayList<>();
//            String security_group = "";
//
//            // ALB Node 생성
//            NodeData AlbNode;
//            AlbNode = makeALb(availableService.alb_index,alb_node_x,alb_node_y);
//
//            // internet gateway to ALB
//            LinkData addIntoALB = createLinkData("Internet", AlbNode.getKey(), key - 1);
//            availableService.linkDataList.add(addIntoALB);
//            // ALB to security Group
//            LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
//            availableService.linkDataList.add(addALBintoGroup);
//
//            // node 추가하기
//            if (node.contains("Security Group")){
//                security_group = node + 2;
//                GroupData new_security_group = createAndConfigureGroupData(security_group, privateSubnetName);
//                if (!availableService.groupDataList.contains(new_security_group)) {
//                    availableService.groupDataList.add(new_security_group); // Add new security group if not present
//                }
//                for (NodeData nodedata : availableService.nodeDataList) {
//                    if (nodedata.getGroup().equals(node)) {
//                        node_temp_list.add(nodedata);
//                    }
//                }
//
//                for (NodeData copy_node : node_temp_list){
//                    node_x += 150;
//                    String newLoc = (node_x) + " " + (node_y);
//                    NodeData copiedNode = new NodeData();
//                    copiedNode.setText(copy_node.getText());
//                    copiedNode.setType(copy_node.getType());
//                    copiedNode.setKey(copy_node.getKey() + availableService.alb_index);
//                    copiedNode.setSource(copy_node.getSource());
//                    copiedNode.setIsGroup(null);
//                    copiedNode.setGroup(security_group);
//                    copiedNode.setLoc(newLoc);
//                    availableService.nodeDataList.add(copiedNode);
//                }
//                System.out.println("security_group: "+new_security_group);
//                LinkData albtogroup = createLinkData(AlbNode.getKey(),new_security_group.getKey(),key -=1);
//                System.out.println("albtogroup: " + albtogroup);
//                availableService.linkDataList.add(albtogroup);
//            }
//
//            alb_node_x += 220;
//            alb_node_y += 10;
//            availableService.nodeDataList.add(AlbNode);
//            availableService.alb_index +=1 ;
//        }
//
//        // Node들간의 연결정보를 추가해서 집어넣어야 함 ... 하 인생 쉬운거 하나도 없네
//
//
//
//
//    }
//
//    public GroupData makeAz(List<GroupData> groupDataList) {
//        GroupData Azdata = new GroupData();
//        Azdata.setKey("Availability Zone2");
//        Azdata.setText("Availability Zone");
//        Azdata.setIsGroup(true);
//        Azdata.setGroup("VPC");
//        Azdata.setType("AWS_Groups");
//        Azdata.setStroke("rgb(0,164,166)");
//
//        groupDataList.add(Azdata);
//        return Azdata;
//    }
//
//    public NodeData makeNat(List<LinkData> linkDataList, List<NodeData> nodeDataList, String publicSubnetName, double natNodeX, double natNodeY, int index) {
//        int nat_sum = 1;
//        for(LinkData linkdata : linkDataList){
//            if(linkdata.getFrom().contains("Public Subnet"))
//            {
//                nat_sum +=1 ;
//            }
//        }
//
//        nat_sum += index;
//        NodeData natnode = new NodeData();
//        natnode.setText("NAT");
//        natnode.setType("Networking-Content-Delivery");
//        natnode.setKey("NAT" + nat_sum);
//        natnode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-VPC_NAT-Gateway_48.svg");
//        String newLoc = (natNodeX) + " " + (natNodeY);
//        natnode.setLoc(newLoc);
//        natnode.setIsGroup(null);
//        natnode.setGroup(publicSubnetName);
//
//        nodeDataList.add(natnode);
//        return natnode;
//
//
//    }
//
//    public NodeData findNodeWithHighestYCoordinate(List<NodeData> nodeDataList) {
//        NodeData highestYNode = null;
//        double highestY = Double.NEGATIVE_INFINITY;
//
//        for (NodeData nodedata : nodeDataList) {
//            String location = nodedata.getLoc();
//            String[] locParts = location.split(" ");
//
//            double y = Double.parseDouble(locParts[1]);
//
//            if (y > highestY) {
//                highestY = y;
//                highestYNode = nodedata;
//            }
//        }
//
//        return highestYNode;
//    }
//
//    public NodeData makeALb(int index, double node_x, double node_y){
//        NodeData AlbNode = new NodeData();
//        AlbNode.setText("Application Load Balancer (ALB)");
//        AlbNode.setKey("Application Load Balancer (ALB)" + index);
//        AlbNode.setFigure("Rectangle");
//        AlbNode.setSource("/img/AWS_icon/Resource_icon/Res_Networking-Content-Delivery/Res_Elastic-Load-Balancing_Application-Load-Balancer_48.svg");
//        AlbNode.setType("Networking-Content-Delivery");
//        String newLoc = (node_x) + " " + (node_y);
//        AlbNode.setLoc(newLoc);
//        AlbNode.setGroup("VPC");
//        System.out.println("ALBNode: "+AlbNode);
//
//        return AlbNode;
//
//    }
//
//    public NodeData createNodeData(String name, String type, String group, String source, String stroke, String newLoc) {
//        NodeData node = new NodeData();
//        node.setKey(name);
//        node.setText(name);
//        node.setIsGroup(true);
//        node.setGroup(group);
//        node.setSource(source);
//        node.setType(type);
//        node.setStroke(stroke);
//        if (newLoc != null && !newLoc.isEmpty()) {
//            node.setLoc(newLoc);
//        }
//        return node;
//    }
//    public LinkData createLinkData(String from, String to, int key) {
//        LinkData link = new LinkData();
//        link.setFrom(from);
//        link.setTo(to);
//        link.setKey(key);
//        return link;
//    }
//    public GroupData createAndConfigureGroupData(String securityGroup, String privateSubnetName) {
//        GroupData group = new GroupData();
//        group.setKey(securityGroup);
//        group.setText("Security Group");
//        group.setIsGroup(true);
//        group.setGroup(privateSubnetName);
//        group.setType("group");
//        group.setStroke("rgb(221,52,76)");
//
//        return group;
//    }
//}
