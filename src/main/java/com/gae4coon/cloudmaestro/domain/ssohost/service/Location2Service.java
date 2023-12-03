package com.gae4coon.cloudmaestro.domain.ssohost.service;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Location2Service {
    public void addPublicLocation(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList, List<String> count_public_subnet) {
        double nat_x = 400;
        double nat_y = -400;
        double node_x;
        double node_y;
        double firewall_x = 200;
        double firewall_y = 200;

        List<String> Except = new ArrayList<>(Arrays.asList("Internet Gateway", "Public subnet", "Private subnet", "NAT Gateway"));

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

        // vpc 찾기
        List<String> count_firewall_endpoints = new ArrayList<>();
        for (GroupData groupdata : groupDataList) {
            if(groupdata.getKey().contains("Firewall Public")){
                count_firewall_endpoints.add(groupdata.getKey());
            }
            System.out.println("Friewall" + groupdata);
        }

        // vpc count 하기
        List<String> vpc_count = new ArrayList<>();
        for (String count_firewall_endpoint : count_firewall_endpoints) {
            for (GroupData groupData : groupDataList) {
                if (groupData.getKey().contains(count_firewall_endpoint)) {
                    vpc_count.add(groupData.getGroup());
                }
            }
        }

        List<String> Az_array = new ArrayList<>();
        for (String vpc : vpc_count) {
            // vpc 에 해당하는 public_subnet 먼저 선별
            for (GroupData groupData : groupDataList) {
                if (groupData.getGroup() != null &&
                        groupData.getGroup().equals(vpc) &&
                        groupData.getKey().contains("Availability")) {
                    Az_array.add(groupData.getKey());
                    break;
                }
            }
        }

        // Create a mapping of VPCs to associated AZs
        Map<String, List<String>> vpcToAzMap = new HashMap<>();

        for (String vpc : vpc_count) {
            // Initialize a list to store associated AZs
            List<String> associatedAzs = new ArrayList<>();

            // Iterate through AZs to find the ones associated with the current VPC
            for (String az : Az_array) {
                for (GroupData groupData : groupDataList) {
                    if (groupData.getGroup() != null &&
                            groupData.getGroup().equals(vpc) &&
                            groupData.getKey().equals(az)) {
                        // Add the AZ to the list of associated AZs
                        associatedAzs.add(az);

                        System.out.println("AZ for VPC " + vpc + ": " + az);
                    }
                }
            }

            // Add the VPC and its associated AZs to the mapping
            vpcToAzMap.put(vpc, associatedAzs);
        }


        for (String vpc : vpc_count) {
            System.out.println("VPC::: " + vpc);
            for (GroupData groupdata : groupDataList) {
                if (groupdata.getGroup() != null &&
                        groupdata.getGroup().equals(vpc) &&
                        groupdata.getKey().contains("Firewall Public")) {
                    String firewallSubnet = groupdata.getKey();
                    for (NodeData firewallnode : nodeDataList) {
                        if (firewallnode.getGroup().equals(firewallSubnet)) {
                            System.out.println("firewallNode: " + firewallnode);
                            String newLoc = (firewall_x) + " " + (firewall_y);
                            firewallnode.setLoc(newLoc);
                        }
                    }
                }
            }
            List<String> associatedAzs = vpcToAzMap.get(vpc);
                if (associatedAzs != null) {
                    for (String az : associatedAzs) {
                        System.out.println("VPC: " + vpc);
                        System.out.println("Associated AZ: " + az);
                        for (GroupData groupdata : groupDataList) {
                            if (groupdata.getGroup() != null &&
                                    groupdata.getGroup().equals(az) &&
                                    groupdata.getKey().contains("Public subnet")
                            ) {
                                String public_subnet = groupdata.getKey();
                                System.out.println("public_subnet ++ " + public_subnet);

                                double[] updatedCoordinates = processPublicSubnet(nodeDataList, public_subnet, nat_x, nat_y);
                                nat_x = updatedCoordinates[0];
                                nat_y = updatedCoordinates[1];
                                String[] parts = public_subnet.split(" ");
                                String netName = parts[0];

                                node_x = nat_x + 430;
                                node_y = nat_y - 85;
                                List<String> visitedNode = new ArrayList<>();

                                for (LinkData currentLink : setNewLinkList) {
                                    for (NodeData nodedata : nodeDataList) {
                                        if (currentLink.getFrom() == null) {
                                            System.out.println("link" + currentLink);
                                            break;
                                        }

                                        // Process private subnet links
                                        if (currentLink.getTo().contains("Private subnet")) {
                                            double[] newCoordinates = processFromPrivateSubnet(currentLink, nodedata, nodeDataList, linkDataList, groupDataList, netName, Except, node_x, node_y, visitedNode);
                                            if (newCoordinates != null) {
                                                node_x = newCoordinates[0];
                                                node_y = newCoordinates[1];
                                            }
                                        }
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

                        }
                    }
                }
        }
        // 마지막 전처리
        // private subnet에 아무것도 포함되지 않은 노드가 있다면 없애버리자

    }

    public double[] processFromPrivateSubnet(LinkData currentLink, NodeData nodedata, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, String netName, List<String> Except, double node_x, double node_y, List<String> visitedNode) {

        // 해당 group이 prod private subnet에 포함됐는 지 확인
        String group_name2 = "";
        if(nodedata.getGroup() != null){
            String[] group_name = nodedata.getGroup().split(" ");
            group_name2 = group_name[0];
        }
        if(group_name2.equals(netName) &&
                !Except.contains(nodedata.getText()) &&
                !visitedNode.contains(nodedata.getKey())){
            System.out.println("NodeData : LinkArea" + nodedata);
            node_x += 20;
            String newLoc = (node_x) + " " + (node_y);
            nodedata.setLoc(newLoc);
            visitedNode.add(currentLink.getTo());
        }
        return new double[]{node_x, node_y};
    }
    public double[] processPublicSubnet(List<NodeData> nodeDataList, String publicSubnet, double nat_x, double nat_y) {
        double x = 0.0;
        double y = 0.0;
        for (NodeData nodeData : nodeDataList) {
            if (nodeData.getGroup()!=null&&nodeData.getGroup().equals(publicSubnet)) {
                String location = nodeData.getLoc();
                String[] locParts = location.split(" ");
                System.out.println("public Subnet" + publicSubnet + nodeData);
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
                if(nodedata.getGroup()!=null&&nodedata.getGroup().equals(security_group)){
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
                if(nodedata.getGroup()!=null&&nodedata.getGroup().equals(security_group)){
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
