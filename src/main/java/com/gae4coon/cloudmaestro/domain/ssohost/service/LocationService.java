package com.gae4coon.cloudmaestro.domain.ssohost.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LocationService {

    public void addPublicLocation(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList, List<String> count_public_subnet, List<String> count_firewall_endpoints) {
        double nat_x = 400;
        double nat_y = -400;
        double node_x = 0;
        double node_y = 0;
        double firewall_x = 200;
        double firewall_y = 200;

        List<String> Except = new ArrayList<>(Arrays.asList("Internet Gateway", "Public subnet", "Private subnet", "NAT Gateway"));

        // 해당 subnet에 해당하는 거 먼저 할게 ..
        List<LinkData> visitedLinks = new ArrayList<>();
        List<LinkData> setNewLinkList = new ArrayList<>();
        for (LinkData linkdata : linkDataList) {
            LinkData nextLink = findNextLink(linkdata, linkDataList, visitedLinks);
            if (nextLink != null && !visitedLinks.contains(nextLink)) {
                visitedLinks.add(nextLink);
                setNewLinkList.add(nextLink);
            }
        }

        System.out.println("count_firewall_endpoints" + count_firewall_endpoints);
        // VPC 의 개수도 알았고 ,, 그럼 어떻게 해야 하지 ..
        // 일단 해당 VPC에 해당이 되는 지도 살펴봐야 하고 ,, 아니 이게 맞냐 .. ?

        // 일단 VPC 의 개수 및 VPC의 종류를 식별을 하자 .
        List<String> vpc_count = new ArrayList<>();
        for (String count_firewall_endpoint : count_firewall_endpoints) {
            for (GroupData groupData : groupDataList) {
                if (groupData.getKey().contains(count_firewall_endpoint)) {
                    vpc_count.add(groupData.getGroup());
                }
            }
        }
        // 해당 vpc만 존재하는 노드들만 다 세팅을 하기 ..

        // public_subnet이 해당 vpc에 대응되는 로직만 짜면 되잖아 ..?
        /// 그리고 마지막에,, vpc에 세팅되는 nat를 옮겨버리자고 ..

        System.out.println("VPC _ Count " + vpc_count);

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

        for (String vpc : vpc_count) {
            // vpc에 해당하는 az 가져오기 ...
            System.out.println("VPC" + vpc);
//            for (GroupData groupdata : groupDataList) {
//                if (groupdata.getGroup() != null &&
//                        groupdata.getGroup().equals(vpc) &&
//                        groupdata.getKey().contains("Firewall Public")) {
//                    String firewallSubnet = groupdata.getKey();
//                    for (NodeData firewallnode : nodeDataList) {
//                        if (firewallnode.getGroup().equals(firewallSubnet)) {
//                            System.out.println("firewallNode: " + firewallnode);
//                            String newLoc = (firewall_x) + " " + (firewall_y);
//                            firewallnode.setLoc(newLoc);
//                        }
//                    }
//                }
//            }
            // 같은 vpc 안에 az인 것들을 찾기
            for (String az : Az_array) {
                for (GroupData groupData : groupDataList) {
                    if (groupData.getGroup() != null &&
                            groupData.getGroup().equals(vpc) &&
                            groupData.getKey().equals(az)) {
                        System.out.println("AZ" + groupData);
                        System.out.println("AZ" + az);

                        for (GroupData groupdata : groupDataList) {
                            if (groupdata.getGroup() != null &&
                                    groupdata.getGroup().equals(az) &&
                                    groupdata.getKey().contains("Public subnet")) {

                                String public_subnet = groupdata.getKey();
                                System.out.println("public_subnet33333" + public_subnet);
                                double[] coordinates = processLinksForSubnet(nodeDataList, linkDataList, setNewLinkList, groupDataList, Except,
                                        node_x, node_y, public_subnet, nat_x, nat_y);

                                // Update nat_x and nat_y with the returned coordinates
                                firewall_x = coordinates[0];
                                firewall_y = coordinates[1];
                                nat_x = coordinates[2];
                                nat_y = coordinates[3];
                                node_x = coordinates[4];
                                node_y = coordinates[5];
                                break;
                            }
                        }
                    }
//
                }

                // Update firewall_x and firewall_y for the next vpc
                // updata nat_x, nat_y

                firewall_x -= 200;
                firewall_y += 200;
            }
        }
    }

    public double[] processLinksForSubnet(List<NodeData> nodeDataList, List<LinkData> linkDataList, List<LinkData> setNewLinkList, List<GroupData> groupDataList, List<String> Except,
                                          double node_x, double node_y, String public_subnet, double nat_x, double nat_y) {
        double[] updatedCoordinates = processPublicSubnet(nodeDataList, public_subnet, nat_x, nat_y);
        nat_x = updatedCoordinates[0];
        nat_y = updatedCoordinates[1];
        String[] parts = public_subnet.split(" ");
        String netName = parts[0];

        double minNodeX = Double.MAX_VALUE;
        double maxNodeY = Double.MIN_VALUE;
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

                // Process 'from' links
                if (currentLink.getFrom().contains("Group")) {
                    double[] newCoordinates = processFromGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y, visitedNode);
                    node_x = newCoordinates[0];
                    node_y = newCoordinates[1];
                }

                // Process 'to' links
                if (currentLink.getTo().contains("Group")) {
                    double[] newCoordinates = processToGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y, visitedNode);
                    node_x = newCoordinates[0];
                    node_y = newCoordinates[1];
                }

                // Update node location and visited nodes
                String group_name2 = "";
                if ((currentLink.getFrom().contains(nodedata.getKey()) || currentLink.getTo().contains(nodedata.getKey())) &&
                        !Except.contains(nodedata.getKey()) && !visitedNode.contains(nodedata.getKey())) {

                    if (nodedata.getGroup() != null) {
                        String[] group_name = nodedata.getGroup().split(" ");
                        group_name2 = group_name[0];
                    }

                    if (group_name2.equals(netName)) {
                        node_x += 20;
                        String newLoc = (node_x) + " " + (node_y);
                        nodedata.setLoc(newLoc);
                        visitedNode.add(currentLink.getFrom().contains(nodedata.getKey()) ? currentLink.getFrom() : currentLink.getTo());

                        // Update minNodeX and maxNodeY
                        if (node_x < minNodeX) {
                            minNodeX = node_x;
                        }
                        if (node_y > maxNodeY) {
                            maxNodeY = node_y;
                        }
                    }
                }
            }
        }

        return new double[] {minNodeX, maxNodeY,nat_x,nat_y,node_x,node_y};
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
