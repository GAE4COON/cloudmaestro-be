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
        double node_x = 0;
        double node_y = 0;
        double firewall_x = 200;
        double firewall_y = -10;

        List<String> Except = new ArrayList<>(Arrays.asList("Internet Gateway", "Public subnet", "Private subnet", "NAT Gateway"));

        // 해당 subnet에 해당하는 거 먼저 할게 ..
        List<LinkData> visitedLinks = new ArrayList<>();
        List<LinkData> setNewLinkList = new ArrayList<>();

        // LinkData 일단 초기 정렬 ( nextLink 찾아서 nexLink 가 나오도록 정렬 해보기 )
        linkDataList = initializeAndSortLinks(linkDataList, visitedLinks, setNewLinkList);

        // VPC 찾기 & VPC_Count 찾는 거 해주기
        List<String> count_firewall_endpoints = findFirewallEndpoints(groupDataList);
        List<String> vpc_count = countVPCsForFirewallEndpoints(count_firewall_endpoints, groupDataList);


        // 해당 vpc에 해당되는 AZ 영역 찾기
        List<String> Az_array = findAzArray(vpc_count, groupDataList);

        // VPC와 AZ Map으로 대응 시키기
        Map<String, List<String>> vpcToAzMap = createVpcToAzMap(vpc_count, Az_array, groupDataList);

        for (String vpc : vpc_count) {
            // vpc 별 firewall 다시 설정
            for (GroupData groupdata : groupDataList) {
                if (groupdata.getGroup() != null &&
                        groupdata.getGroup().equals(vpc) &&
                        groupdata.getKey().contains("Firewall Public")) {
                    String firewallSubnet = groupdata.getKey();
                    for (NodeData firewallnode : nodeDataList) {
                        if (firewallnode.getGroup()!=null&&firewallnode.getGroup().equals(firewallSubnet)) {
                            String newLoc = (firewall_x) + " " + (firewall_y);
                            firewallnode.setLoc(newLoc);
                        }
                    }
                }
            }
            double minNodeX = Double.MAX_VALUE; // 초기값을 가장 큰 값으로 설정
            double maxNodeY = Double.MIN_VALUE;
            List<String> associatedAzs = vpcToAzMap.get(vpc);
                if (associatedAzs != null) {
                    for (String az : associatedAzs) {
                        System.out.println("AvailableZone" + az);
                        Set<String> processedPublicSubnets = new HashSet<>();
                        for (GroupData groupdata : groupDataList) {
                            if (groupdata.getGroup() != null &&
                                    groupdata.getGroup().equals(az) &&
                                    groupdata.getKey().contains("Public subnet")
                            ) {

                                double[] coordinates = processPublicSubnetAndLinks(groupDataList, az, linkDataList, nodeDataList, nat_x, nat_y, Except, node_x, node_y, minNodeX, maxNodeY,processedPublicSubnets);

                                nat_x = coordinates[0];
                                nat_y = coordinates[1];
                                node_x = coordinates[2];
                                node_y = coordinates[3];
                                minNodeX = coordinates[4];
                                maxNodeY = coordinates[5];
                            }
                        }

                    }
                }

            firewall_x = minNodeX - 600;
            firewall_y = maxNodeY + 400;
            nat_x = firewall_x + 200;
            nat_y = firewall_y - 20;

        }

    }
    public double[] processPublicSubnetAndLinks(List<GroupData> groupDataList, String az, List<LinkData> linkDataList, List<NodeData> nodeDataList, double nat_x, double nat_y, List<String> Except, double node_x, double node_y, double minNodeX, double maxNodeY, Set<String> processedPublicSubnets) {
        double[] coordinates = new double[6];

        for (GroupData groupdata : groupDataList) {
            if (groupdata.getGroup() != null &&
                    groupdata.getGroup().equals(az) &&
                    groupdata.getKey().contains("Public subnet")
            ) {
                String public_subnet = groupdata.getKey();

                if (processedPublicSubnets.contains(public_subnet)) {
                    continue;
                }

                processedPublicSubnets.add(public_subnet);

                double[] updatedCoordinates = processPublicSubnet(nodeDataList, public_subnet, nat_x, nat_y);
                nat_x = updatedCoordinates[0];
                nat_y = updatedCoordinates[1];
                String[] parts = public_subnet.split(" ");
                String netName = parts[0];

                node_x = nat_x + 430;
                node_y = nat_y - 85;
                List<String> visitedNode = new ArrayList<>();

                for (LinkData currentLink : linkDataList) {
                    for (NodeData nodedata : nodeDataList) {
                        if (currentLink.getFrom() == null) {
                            break;
                        }


                        if (currentLink.getFrom().contains("Group")) {
                            double[] newCoordinates = processFromGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y, visitedNode);
                            node_x = newCoordinates[0];
                            node_y = newCoordinates[1];
                        }
                        if (currentLink.getTo().contains("Group")) {
                            double[] newCoordinates = processToGroupData(currentLink, nodedata, groupDataList, netName, Except, node_x, node_y, visitedNode);
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
                        // Process private subnet links ( NodeData인데 Link 정보가 없는 경우 )
                        if (currentLink.getTo().contains("Private subnet")) {
                            double[] newCoordinates = processFromPrivateSubnet(currentLink, nodedata, nodeDataList, linkDataList, groupDataList, netName, Except, node_x, node_y, visitedNode);
                            if (newCoordinates != null) {
                                node_x = newCoordinates[0];
                                node_y = newCoordinates[1];
                            }
                        }
                        minNodeX = Math.min(minNodeX, node_x);
                        maxNodeY = Math.max(maxNodeY, node_y);
                    }
                }
            }
        }

        // 반환할 좌표 배열 설정
        coordinates[0] = nat_x;
        coordinates[1] = nat_y;
        coordinates[2] = node_x;
        coordinates[3] = node_y;
        coordinates[4] = minNodeX;
        coordinates[5] = maxNodeY;

        return coordinates;
    }


    public Map<String, List<String>> createVpcToAzMap(List<String> vpc_count, List<String> Az_array, List<GroupData> groupDataList) {
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
                    }
                }
            }

            // Add the VPC and its associated AZs to the mapping
            vpcToAzMap.put(vpc, associatedAzs);
        }

        return vpcToAzMap;
    }
    public List<String> findFirewallEndpoints(List<GroupData> groupDataList) {

        List<String> count_firewall_endpoints = new ArrayList<>();
        for (GroupData groupdata : groupDataList) {
            if(groupdata.getText().contains("Firewall Public Subnet")){
                count_firewall_endpoints.add(groupdata.getKey());
            }

        }
           return count_firewall_endpoints;
    }

    public List<String> findAzArray(List<String> vpc_count, List<GroupData> groupDataList) {
         List<String> Az_array = new ArrayList<>();
        for (String vpc : vpc_count) {
            // vpc에 해당하는 public_subnet 먼저 선별
            for (GroupData groupData : groupDataList) {
                if (groupData.getGroup() != null &&
                        groupData.getGroup().equals(vpc) &&
                        groupData.getKey().contains("Availability")) {
                    Az_array.add(groupData.getKey());
                    break;
                }
            }
        }
        return Az_array;
    }

    public List<String> countVPCsForFirewallEndpoints(List<String> count_firewall_endpoints, List<GroupData> groupDataList) {
        List<String> vpc_count = new ArrayList<>();

        for(GroupData groupdata : groupDataList){
            if(groupdata.getKey().contains("VPC") ||
                groupdata.getKey().contains("vpc")){
                vpc_count.add(groupdata.getKey());
            }
        }

        return vpc_count;
    }

    public List<LinkData> initializeAndSortLinks(List<LinkData> linkDataList, List<LinkData> visitedLinks, List<LinkData> setNewLinkList) {
        for (LinkData linkdata : linkDataList) {
            LinkData nextLink = findNextLink(linkdata, linkDataList, visitedLinks);
            if (nextLink != null && !visitedLinks.contains(nextLink)) {
                visitedLinks.add(nextLink);
                setNewLinkList.add(nextLink);
            }
        }
        return setNewLinkList; // setNewLinkList 반환
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

    public void setNodeLocation(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {

        // Group 정보에서 public subnet이 몇 개인지 확인
        List<String> count_public_subnets = new ArrayList<>();
        List<String> count_firewall_endpoints = new ArrayList<>();
        for (GroupData groupdata : groupDataList) {
            if (groupdata.getKey().contains("Public subnet")) {
                count_public_subnets.add(groupdata.getKey());
            }
            if(groupdata.getKey().contains("Firewall Public")){
                count_firewall_endpoints.add(groupdata.getKey());
            }
        }

        // LinkData Public Subnet 별로 순서 정하기

        // LinkData 정렬
        linkDataList.sort(Comparator.comparing(LinkData::getFrom).thenComparing(LinkData::getTo));

        Iterator<LinkData> iterator = linkDataList.iterator();
        while (iterator.hasNext()) {
            LinkData linkData = iterator.next();
            if (linkData.getFrom().contains("Shield")) {
                iterator.remove();
            }
        }

        // public subnet을 일단 internet gateway를 기반으로 위치 정하기
        addPublicLocation(nodeDataList, groupDataList, linkDataList, count_public_subnets);
    }
}
