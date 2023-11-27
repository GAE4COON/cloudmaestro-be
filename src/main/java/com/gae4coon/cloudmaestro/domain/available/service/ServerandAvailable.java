package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServerandAvailable {

    public List<LinkData> ServerandAvailable(List<LinkData> linkDataList, List<GroupData> groupDataList, List<NodeData> nodeDataList, List<String> availableNodes, double node_x, double node_y, int key, String privateSubnetName,int alb_index, double alb_node_x, double alb_node_y) {
        // 삭제할 linked list 가져오기
        List<LinkData> delete_linked_list = new ArrayList<>();

        for(String node : availableNodes)
        {
            // NodeData 복사 시작
            List<NodeData> node_temp_list = new ArrayList<>();
            String security_group = "";

            // ALB Node 생성makeALBandInternet
            NodeData AlbNode;

            // node 추가하기
            boolean includeGroup = true;
            boolean includeALB = true;
            System.out.println("groupDataList : " + groupDataList);
            if (node.contains("Security Group")){

                security_group= node + "a";
                AlbNode = makeALb(alb_index,alb_node_x,alb_node_y);

                // internet gateway to ALB
                LinkData addIntoALB = createLinkData("Internet Gateway", AlbNode.getKey(), key - 1);
                linkDataList.add(addIntoALB);

                // ALB to 기존 Node
                LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
                linkDataList.add(addALBintoGroup);
                nodeDataList.add(AlbNode);
                // 이미 기존에 있는 그룹일테니깐 ... 그 그룹에다가 선택을 하는 거지 ...
                for (GroupData groupdata : groupDataList){

                    if(groupdata.getKey().equals(security_group)){
                        GroupData new_security_group = groupdata;
                        addLinkSecurityGroup(new_security_group,linkDataList,AlbNode, key-=1,node_x,node_y);
                        includeGroup = false;
                        break;
                    }
                }
                if(includeGroup){
                    // 새로운 그룹 생성하고 그룹과 alb의 연결
                    GroupData new_security_group = createAndConfigureGroupData(security_group, privateSubnetName);
                    addSecurityGroup(node,security_group,new_security_group,groupDataList,nodeDataList,linkDataList,node_temp_list,node_x,node_y,AlbNode, key-=1, alb_index);
                }

            }

            else if(node.contains("EC2")){

                // Ec2를 선택을 했는데 같은 계층에 ec2 중 auto scaling group 이 있다면 그것과 연결을 하기
                String To = "";
                boolean nodeExists = false;
                for(LinkData linkData : linkDataList){
                    if(linkData.getFrom().equals(node)){
                        To = linkData.getTo();
                        break;
                    }
                }

                System.out.println("To" + To);

                boolean includeEc2 = false;
                // 같은 To를 향한 node 중에 auto scaling group이 있다면 ?
                for(LinkData linkData : linkDataList){
                    if(linkData.getTo().equals(To)){
                        for(NodeData nodeData : nodeDataList){
                            if(nodeData.getKey().equals(linkData.getFrom()) &&
                                    nodeData.getGroup().contains("Auto Scaling")){
                                node = nodeData.getKey();
                                includeEc2 = true;
                                break;
                            }
                        }
                    }
                }

                for(LinkData linkdata : linkDataList){
                    if(linkdata.getFrom().contains("ALB") && linkdata.getTo().equals(node)){
                        includeALB = false;
                    }
                }

                if(includeEc2 && includeALB){
                    AlbNode = makeALb(alb_index,alb_node_x,alb_node_y);

                    // internet gateway to ALB
                    LinkData addIntoALB = createLinkData("Internet Gateway", AlbNode.getKey(), key - 1);
                    linkDataList.add(addIntoALB);

                    // ALB to 기존 Node
                    LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
                    linkDataList.add(addALBintoGroup);

                    LinkData addALBintoEC2 = createLinkData(AlbNode.getKey(), node, key - 1);
                    nodeDataList.add(AlbNode);
                    if(!linkDataList.contains(addALBintoEC2)){
                        System.out.println("albtogroup: " + addALBintoEC2);
                        linkDataList.add(addALBintoEC2);
                    }

                    LinkData albtogroup = createLinkData(AlbNode.getKey(),node+"a",key-=1);
                    if(!linkDataList.contains(albtogroup)){
                        System.out.println("albtogroup: " + albtogroup);
                        linkDataList.add(albtogroup);
                    }

                }
                if(!includeEc2){
                    AlbNode = makeALb(alb_index,alb_node_x,alb_node_y);

                    // internet gateway to ALB
                    LinkData addIntoALB = createLinkData("Internet Gateway", AlbNode.getKey(), key - 1);
                    linkDataList.add(addIntoALB);

                    // ALB to 기존 Node
                    LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
                    linkDataList.add(addALBintoGroup);
                    nodeDataList.add(AlbNode);
                    double[] newCoordinates = addNode(node,groupDataList,nodeDataList,linkDataList,AlbNode,node_x,node_y,privateSubnetName, key-=1);
                    node_x = newCoordinates[0];
                    node_y = newCoordinates[1];
                }

            }

            alb_index +=1 ;
            alb_node_x += 220;
            alb_node_y += 10;

        }

        return delete_linked_list;

    }

    public void makeALBandInternet(NodeData AlbNode, List<LinkData> linkDataList, int albIndex, double albNodeX, double albNodeY, int key, String node, int alb_index, double alb_node_x, double alb_node_y) {
        AlbNode = makeALb(alb_index,alb_node_x,alb_node_y);

        // internet gateway to ALB
        LinkData addIntoALB = createLinkData("Internet Gateway", AlbNode.getKey(), key - 1);
        linkDataList.add(addIntoALB);

        // ALB to 기존 Node
        LinkData addALBintoGroup = createLinkData(AlbNode.getKey(), node, key - 1);
        linkDataList.add(addALBintoGroup);

        key -= 1;
    }

    public void linkEc2andALB(NodeData AlbNode, String node, int key,List<LinkData> linkDataList) {
        LinkData addALBintoEC2 = createLinkData(AlbNode.getKey(), node, key - 1);
        if(!linkDataList.contains(addALBintoEC2)){
            System.out.println("albtogroup: " + addALBintoEC2);
            linkDataList.add(addALBintoEC2);
        }

        LinkData albtogroup = createLinkData(AlbNode.getKey(),node+"a",key-=1);
        if(!linkDataList.contains(albtogroup)){
            System.out.println("albtogroup: " + albtogroup);
            linkDataList.add(albtogroup);
        }
    }

    private double[] addLinkSecurityGroup( GroupData new_security_group, List<LinkData> linkDataList, NodeData albNode, int key,double node_x, double node_y) {

        // ALB Node와 연결
        LinkData albtogroup = createLinkData(albNode.getKey(),new_security_group.getKey(),key-=1);
        System.out.println("albtogroup: " + albtogroup);
        linkDataList.add(albtogroup);
        return new double[]{node_x, node_y};

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


    public double[] addSecurityGroup(String node, String security_group, GroupData new_security_group, List<GroupData> groupDataList, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<NodeData> node_temp_list, double node_x, double node_y, NodeData AlbNode, int key, int alb_index) {
        if (!groupDataList.contains(new_security_group)) {
            groupDataList.add(new_security_group); // Add new security group if not present
        }
        for (NodeData nodedata : nodeDataList) {
            if (nodedata.getGroup().equals(node)) {
                System.out.println("기존의 있는 nodedata +" + node);
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


    public double[] addNode(String node, List<GroupData> groupDataList, List<NodeData> nodeDataList, List<LinkData> linkDataList, NodeData AlbNode, double node_x, double node_y, String privateSubnetName, int Key) {
        String node_name = node + "a";
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
}
