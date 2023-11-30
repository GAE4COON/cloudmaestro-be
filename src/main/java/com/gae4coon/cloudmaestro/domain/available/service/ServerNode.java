package com.gae4coon.cloudmaestro.domain.available.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class ServerNode {
    public void ServerNode(List<String> serverNode, List<LinkData> linkDataList, List<GroupData> groupDataList, List<NodeData> nodeDataList, double node_x, double node_y, int key, String privateSubnetName, String originalprivatesubnetname, List<LinkData> deleteLink,int Auto_index) {

        int text_index;
        List<String> exceptNode2 = new ArrayList<>();  // 리스트 초기화
        for(String node : serverNode){
            List<NodeData> exceptNode = new ArrayList<>();
            // Node 1개만 빼고 제외하기
            if (node.contains("Security Group")){
                // 먼저 노드 하나만 빼고 제외하기
                ExceptNode(node, nodeDataList, groupDataList, exceptNode);
                text_index =  Auto_index;
                // 기존에 있는 그룹을 Auto Group으로 변화시키기
                GroupData newAutoGroup =  createAutoGroup(node, Auto_index, text_index,nodeDataList, groupDataList,originalprivatesubnetname);

                Auto_index += 1;

                // 일단 AutoGroup은 복사를 한다.
                GroupData CopyAutoGroup = createAutoGroup(node + "a" , Auto_index, text_index, nodeDataList, groupDataList,privateSubnetName);
                System.out.println("CopyAutoGroup: "+ CopyAutoGroup);

                // NodeData 순환하기
                List<NodeData> newNodes = new ArrayList<>(); // 새로운 노드를 저장할 리스트

                for (NodeData nodedata : nodeDataList) {
                    if (nodedata.getGroup().equals(node)) {
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
                    addCopyNode(exceptNode2, groupDataList, nodeDataList, privateSubnetName, Auto_index, text_index,node_x, node_y);

                    removeNode(exceptNode2, linkDataList, nodeDataList,deleteLink);
                }
            }

            Auto_index += 1;
        }

        Auto_index += 1;

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

    public void addCopyNode(List<String> exceptNode, List<GroupData> groupDataList, List<NodeData> nodeDataList, String privateSubnetName, int autoIndex, int text_index,double node_x, double node_y) {
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
                copiedNode.setKey(nodedata.getKey() + "a");
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

    public void removeNode(List<String> exceptNode, List<LinkData> linkDataList, List<NodeData> nodeDataList,List<LinkData> deleteLink) {
        List<NodeData> exceptNode2 = new ArrayList<>(); // 올바른 리스트 초기화

        for (int i = 0; i < exceptNode.size(); i++) {
            for (NodeData nodedata : nodeDataList) {
                if (nodedata.getKey().equals(exceptNode.get(i)) && nodedata.getGroup().contains("Auto Scaling group")) {
                    // Auto Scaling group에 속하는 노드는 제외
                } else if (nodedata.getKey().equals(exceptNode.get(i))) {
                    // Auto Scaling group에 속하지 않는 노드는 삭제 목록에 추가
                    for(LinkData linkdata : linkDataList){
                        if(linkdata.getFrom().equals(nodedata.getKey())){
                            deleteLink.add(linkdata);
                        }
                    }
                    exceptNode2.add(nodedata);
                }
            }
        }
        nodeDataList.removeAll(exceptNode2); // 삭제 목록에 있는 모든 노드 제거
    }
}
