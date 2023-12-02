package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LocationService {

    public void addPublicLocation(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList, List<String> count_public_subnet) {
        double nat_x = 400;
        double nat_y = -400;
        double node_x;
        double node_y;

        List<String> Except = new ArrayList<>(Arrays.asList("Internet Gateway", "Public subnet", "Private subnet"));

        // 해당 subnet에 해당하는 거 먼저 할게 ..
        List<LinkData> visitedLinks = new ArrayList<>();
        List<LinkData> setNewLinkList = new ArrayList<>();

        for(LinkData linkdata : linkDataList){
            LinkData nextLink = findNextLink(linkdata, linkDataList, visitedLinks);
            if (nextLink != null && !visitedLinks.contains(nextLink)) {
                visitedLinks.add(nextLink);
                setNewLinkList.add(nextLink);
            }

        }

        for (String public_subnet : count_public_subnet) {

            System.out.println("public_subnet" + public_subnet);

            double[] updatedCoordinates = processPublicSubnet(nodeDataList, public_subnet, nat_x, nat_y);
            nat_x = updatedCoordinates[0];
            nat_y = updatedCoordinates[1];
            String[] parts = public_subnet.split(" ");
            String netName = parts[0];
            node_x = nat_x + 430;
            node_y = nat_y - 85;
            List<String> visitedNode = new ArrayList<>();
            for (LinkData currentLink : setNewLinkList) {
                // 처음 방문한 linkdata에 대해서
                for (NodeData nodedata : nodeDataList) {
                    if(currentLink.getFrom() == null) {
                        System.out.println("link" + currentLink);
                        break;
                    }
                    //linkdata에 node 관련 정보는 없고 private subnet에 달랑 한개만 있을 때
//                        if(currentLink.getTo().contains("Private subnet")){
//                            double[] newCoordinates = processFromPrivateSubnet(currentLink, nodedata, nodeDataList,linkDataList,groupDataList, netName, Except, node_x, node_y,visitedNode);
//                            node_x = newCoordinates[0];
//                            node_y = newCoordinates[1];
//                        }
                    if (currentLink.getFrom().contains("Group")) {
                        double[] newCoordinates = processFromGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y,visitedNode);
                        node_x = newCoordinates[0];
                        node_y = newCoordinates[1];
                    }
                    if (currentLink.getTo().contains("Group")) {
                        double[] newCoordinates = processToGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y,visitedNode);
                        node_x = newCoordinates[0];
                        node_y = newCoordinates[1];
                    }
                    String group_name2 = "";
                    if (currentLink.getFrom().contains(nodedata.getKey()) &&
                            !Except.contains(nodedata.getKey())) {
                        //System.out.println("currentLink" + currentLink);
                        if(nodedata.getGroup() != null){
                            String[] group_name = nodedata.getGroup().split(" ");
                            group_name2 = group_name[0];
                        }
                        if(group_name2.equals(netName)){
                            node_x += 20;
                            String newLoc = (node_x) + " " + (node_y);
                            nodedata.setLoc(newLoc);
                            visitedNode.add(currentLink.getFrom());
                        }

                    }
                    if (currentLink.getTo().contains(nodedata.getKey()) &&
                            !Except.contains(nodedata.getKey())) {
                        if(nodedata.getGroup() != null){
                            String[] group_name = nodedata.getGroup().split(" ");
                            group_name2 = group_name[0];
                        }
                        if(group_name2.equals(netName)){
                            node_x += 20;
                            String newLoc = (node_x) + " " + (node_y);
                            nodedata.setLoc(newLoc);
                            visitedNode.add(currentLink.getTo());
                        }

                    }
                }
            }
        }

        // 마지막 전처리
        // private subnet에 아무것도 포함되지 않은 노드가 있다면 없애버리자
    }

    public double[] processPublicSubnet(List<NodeData> nodeDataList, String publicSubnet, double nat_x, double nat_y) {
        double x = 0.0;
        double y = 0.0;
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getGroup().contains(publicSubnet)) {
                String location = nodeData.getLoc();
                String[] locParts = location.split(" ");
                System.out.println("public Subnet" + publicSubnet);
                x = nat_x - 1;
                y = nat_y + 260;
                String newLoc = (x) + " " + (y);
                System.out.println("newLoc" + newLoc);
                nat_x -= 1;
                nat_y += 260;
                nodeData.setLoc(newLoc);
                break;
            }
        }
        return new double[]{nat_x, nat_y};
    }

    private double[] processFromGroupData(LinkData linkdata, NodeData nodedata, List<GroupData> groupDataList, String netName, List<String> Except, double node_x, double node_y , List<String> visitedNodes) {
        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String security_group = linkdata.getFrom();
        for(GroupData group : groupDataList){
            String group_name2 = group.getGroup();
            if(group.getGroup() != null){
                String[] group_name = group.getGroup().split(" ");
                group_name2 = group_name[0];
            }

            if(group.getKey().equals(security_group) &&
                    group_name2.equals(netName) &&
                    !Except.contains(security_group) &&
                    !visitedNodes.contains(nodedata.getKey())
                // 새로운 security 요소여야 함 &&
            ){
                // 포함되는 게 확인됐다면, 그룹 내의 요소들 가져오기
                if(nodedata.getGroup().equals(security_group)){
                    System.out.println("Group에 있는 NodeData" + nodedata + security_group + group_name2);node_x += 150;
                    String newLoc = (node_x) + " " + (node_y);
                    nodedata.setLoc(newLoc);
                    visitedNodes.add(nodedata.getKey());

                }

            }
        }
        return new double[]{node_x, node_y};
    }
    private  double[] processToGroupData(LinkData linkdata, NodeData nodedata, List<GroupData> groupDataList, String netName,  List<String> Except, double node_x, double node_y, List<String> visitedNodes) {

        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String security_group = linkdata.getTo();
        System.out.println("securityGroup v19191919" + security_group);
        for(GroupData group : groupDataList){
            String group_name2 = group.getGroup();
            if(group.getGroup() != null){
                String[] group_name = group.getGroup().split(" ");
                group_name2 = group_name[0];
            }

            if(group.getKey().equals(security_group) &&
                    group_name2.equals(netName) &&
                    !Except.contains(security_group) &&
                    !visitedNodes.contains(nodedata.getKey())
                // 새로운 security 요소여야 함 &&
            )
                //visitGroup.add(security_group);
                // 포함되는 게 확인됐다면, 그룹 내의 요소들 가져오기
                if(nodedata.getGroup().equals(security_group)){
                    System.out.println("group include nodedata2 : "+nodedata);
                    node_x += 150;
                    String newLoc = (node_x) + " " + (node_y);
                    nodedata.setLoc(newLoc);
                    visitedNodes.add(nodedata.getKey());

                }

        }

        return new double[]{node_x, node_y};
    }

    private LinkData findNextLink(LinkData currentLink, List<LinkData> linkDataList, List<LinkData> visitedLinks) {

        // 그냥 단순한 순서 정렬을 하자고 ..
        for (LinkData link : linkDataList) {
            if(currentLink.getFrom().equals(link.getTo()) &&
                    !visitedLinks.contains(link)){
                return link;
            }
            if(currentLink.getTo().equals(link.getFrom()) &&
                    !visitedLinks.contains(link)){
                return link;
            }


        }
        return currentLink; // No next link found
    }
}
