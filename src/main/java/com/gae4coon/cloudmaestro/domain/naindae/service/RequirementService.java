package com.gae4coon.cloudmaestro.domain.naindae.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequirementService {
    public Map<String, Object> getRequirementAvailable(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
            int i = 0;
            
            //TODO 망별로 처리하기 위해 zones에 name을 확인하여 해당 망에서만 작업할 수 있도록
            
            Map<String, Object> result = new HashMap<>();
            NodeData availabilityZoneNode = new NodeData();
            availabilityZoneNode.setKey("Availability Zone2");
            availabilityZoneNode.setText("Availability Zone");
            availabilityZoneNode.setIsGroup(true);
            availabilityZoneNode.setGroup("VPC");
            availabilityZoneNode.setType("AWS_Groups");
            availabilityZoneNode.setStroke("rgb(0,164,166)");
            nodeDataList.add(availabilityZoneNode);

            Point2D location = findLoc(nodeDataList, groupDataList);

            System.out.println("loc: "+location);


            GroupData newPrivateSubnet = new GroupData();
            newPrivateSubnet.setKey("Multi-AZ Private subnet");
            newPrivateSubnet.setText("Multi-AZ Private subnet");
            newPrivateSubnet.setIsGroup(true);
            newPrivateSubnet.setGroup("Availability Zone2");
            newPrivateSubnet.setType("AWS_Groups");
            newPrivateSubnet.setStroke("rgb(0,164,166)");
            groupDataList.add(newPrivateSubnet);

            Map<String, Object> rdsResult = countRdsNodesInPrivateSubnets(nodeDataList,groupDataList);
            int rdsCount = (int) rdsResult.get("rdsNodeCount");

            List<String> groupKeys = (List<String>) rdsResult.get("groupKeys");
            int countNodes = countNodes(nodeDataList);

            for (String key : groupKeys) {
                    countNodes++;
                    String newLoc = (location.getX()+(i*100)-200) + " " + (location.getY()+400);
                    NodeData rdsNode = new NodeData();
                    rdsNode.setKey("RDS"); // NAT 키를 고유하게 만듦
                    rdsNode.setText("RDS"+countNodes);
                    rdsNode.setLoc(newLoc); // 계산된 위치 설정
                    rdsNode.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                    rdsNode.setType("Arch_Database");
                    rdsNode.setGroup(newPrivateSubnet.getKey());
                    nodeDataList.add(rdsNode);

                    //TODO 여기에 link로 연결해주어야함
                    LinkData link = new LinkData();
                    link.setFrom(key);
                    link.setTo("RDS"+countNodes); //여기에 public subnet이 와야함
                    linkDataList.add(link);

                    i++;
            }


            //TODO link로

            result.put("nodes", nodeDataList);
            result.put("groups", groupDataList);

            return result;

    }
        public Map<String, Object> countRdsNodesInPrivateSubnets(List<NodeData> nodeDataList, List<GroupData> groupDataList) {
                int rdsNodeCount = 0;
                List<String> matchingGroupKeys = new ArrayList<>();
                Map<String, Object> result = new HashMap<>();
                // "private subnet" 문자열이 포함된 그룹 찾기
                List<GroupData> privateSubnetGroups = groupDataList.stream()
                        .filter(group -> group.getKey().contains("private subnet"))
                        .collect(Collectors.toList());

                // 각 private subnet 그룹에 대해
                for (GroupData group : groupDataList) {
                        if (group.getKey().toLowerCase().contains("security group")) {
                                // 해당 그룹의 노드들 중에서 "RDS" 문자열을 포함하는 노드 찾기
                                for (NodeData node : nodeDataList) {
                                        if (node.getGroup() != null && node.getGroup().equals(group.getKey()) && node.getText().toLowerCase().contains("rds")) {
                                                rdsNodeCount++;
                                                matchingGroupKeys.add(group.getKey());
                                                break;
                                        }
                                }
                        }
                }
                result.put("rdsNodeCount", rdsNodeCount);
                result.put("groupKeys", matchingGroupKeys);
                return result;
        }

        public Point2D findLoc(List<NodeData> nodeDataList, List<GroupData> groupDataList){
                List<String> privateSubnetGroupKeys = new ArrayList<>();
                for (GroupData group : groupDataList) {
                        if (group.getKey().contains("Private subnet")) {
                                privateSubnetGroupKeys.add(group.getKey());
                        }
                }
                double maxY = Double.MIN_VALUE;
                double minX = Double.MAX_VALUE;
                for (String privateSubnetGroupKey : privateSubnetGroupKeys) {
                        for (NodeData node : nodeDataList) {
                                for (GroupData groupData : groupDataList) { //group를 순회한다
                                        if (groupData.getKey().equals(node.getGroup())) {
                                                if (groupData.getGroup() != null && groupData.getGroup().equals(privateSubnetGroupKey)) {
                                                        String location = node.getLoc();
                                                        String[] locParts = location.split(" ");

                                                        double x = Double.parseDouble(locParts[0]);
                                                        double y = Double.parseDouble(locParts[1]);
                                                        if (y > maxY || (y == maxY && x < minX)) { // y축이 -일때 위로 올라간다 x는 -일때 왼쪽이 맞음
                                                                maxY = y;
                                                                minX = x;
                                                        }
                                                }
                                        }
                                }
                        }
                }

                return new Point2D.Double(minX, maxY);
        }

        public int countNodes(List<NodeData> nodeDataList) {
                int nodeCount = 0;
                for (NodeData node : nodeDataList) {
                        if (node.getKey().contains("RDS") || node.getText().contains("RDS")) {
                                nodeCount++;
                        }
                }
                return nodeCount;
        }

}
