package com.gae4coon.cloudmaestro.domain.naindae.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequirementService {
    public void getRequirementAvailable(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList) {
            List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();
            List<ZoneDTO> Zones = requireDiagramDTO.getRequirementData().getZones();
            if (!Zones.isEmpty()) {
                    for (ZoneDTO zone : Zones) {
                            if (zone.getZoneRequirements().contains("데이터베이스 분산")) {
                                    processRegions(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
                            }
                    }
            } else if (globalRequirements.contains("데이터베이스 분산 (RDS)") || globalRequirements.contains("리소스 이중화")) {
                    processRegions(requireDiagramDTO, nodeDataList, linkDataList, groupDataList);
            }
    }

        private void processRegions(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList) {
                int regionIndex = 0;
                List<GroupData> regions = groupDataList.stream()
                        .filter(group -> "AWS_Groups".equals(group.getType()) && group.getKey().contains("Region"))
                        .collect(Collectors.toList());

                for (GroupData region : regions) {
                        Optional<GroupData> vpcGroup = groupDataList.stream()
                                .filter(group -> group.getKey().contains("VPC") && group.getGroup() != null && group.getGroup().equals(region.getKey()))
                                .findFirst();
                        Point2D location = findLocForRegion(nodeDataList, groupDataList, region.getKey());

                        if (vpcGroup.isPresent()) {
                                Map<String, Object> rdsNodeCountInfo = countRdsNodesInPrivateSubnets(nodeDataList, groupDataList);
                                List<String> rdsSubnetKeys = (List<String>) rdsNodeCountInfo.get("groupKeys");
                                List<String> relevantSubnetKeys = regionIndex == 0 ? rdsSubnetKeys.stream()
                                        .filter(key -> !key.startsWith("MR-"))
                                        .collect(Collectors.toList()) :
                                        rdsSubnetKeys.stream()
                                                .filter(key -> key.startsWith("MR-"))
                                                .collect(Collectors.toList());

                                createRds(nodeDataList, linkDataList, groupDataList, region, location, relevantSubnetKeys, regionIndex == 0);
                                regionIndex++;
                        }
                }
        }
        private void createRds(List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, GroupData region, Point2D location, List<String> subnetKeys, boolean isFirstRegion) {
                int i = 0;
                String vpcKey = region.getKey();
                String azKey = region.getKey() + "Multi-AZ";

                // AZ 및 Private Subnet 노드 생성
                NodeData availabilityZoneNode = new NodeData();
                availabilityZoneNode.setKey(azKey);
                availabilityZoneNode.setText(azKey);
                availabilityZoneNode.setIsGroup(true);
                availabilityZoneNode.setGroup(vpcKey);
                availabilityZoneNode.setType("AWS_Groups");
                availabilityZoneNode.setStroke("rgb(0,164,166)");
                nodeDataList.add(availabilityZoneNode);

                GroupData newPrivateSubnet = new GroupData();
                newPrivateSubnet.setKey(azKey + "Private subnet");
                newPrivateSubnet.setText(azKey + "Private subnet");
                newPrivateSubnet.setIsGroup(true);
                newPrivateSubnet.setGroup(azKey);
                newPrivateSubnet.setType("AWS_Groups");
                newPrivateSubnet.setStroke("rgb(0,164,166)");
                groupDataList.add(newPrivateSubnet);

                // RDS 노드 생성 및 연결
                for (String subnetKey : subnetKeys) {
                        String newLoc = location.getX() + (i * 100) + " " + location.getY();

                        String rdsKey = availabilityZoneNode.getKey() + "RDS"; // RDS 키 생성
                        NodeData rdsNode = new NodeData();
                        rdsNode.setKey(rdsKey);
                        rdsNode.setText("RDS");
                        rdsNode.setLoc(newLoc);
                        rdsNode.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                        rdsNode.setType("Arch_Database");
                        rdsNode.setGroup(newPrivateSubnet.getKey());
                        nodeDataList.add(rdsNode);

                        // RDS 노드와 서브넷을 연결하는 링크 생성
                        LinkData link = new LinkData();
                        link.setFrom(subnetKey);
                        link.setTo(newPrivateSubnet.getKey());
                        linkDataList.add(link);
                        i++;
                }
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


        public int countNodes(List<NodeData> nodeDataList) {
                int nodeCount = 0;
                for (NodeData node : nodeDataList) {
                        if (node.getKey().contains("RDS") || node.getText().contains("RDS")) {
                                nodeCount++;
                        }
                }
                return nodeCount;
        }

        public Point2D findLocForRegion(List<NodeData> nodeDataList, List<GroupData> groupDataList, String regionKey){
                double maxY = Double.MIN_VALUE;
                double maxX = Double.MIN_VALUE;

                // 각 그룹 키를 찾는 코드
                List<String> vpcGroupKeys = groupDataList.stream()
                        .filter(group -> group.getGroup() != null && group.getGroup().equals(regionKey) && group.getKey().contains("VPC"))
                        .map(GroupData::getKey)
                        .collect(Collectors.toList());

                List<String> availabilityGroupKeys = groupDataList.stream()
                        .filter(group -> vpcGroupKeys.contains(group.getGroup()) && group.getKey().contains("Availability"))
                        .map(GroupData::getKey)
                        .collect(Collectors.toList());

                List<String> privateGroupKeys = groupDataList.stream()
                        .filter(group -> availabilityGroupKeys.contains(group.getGroup()) && group.getKey().contains("Private"))
                        .map(GroupData::getKey)
                        .collect(Collectors.toList());

                List<String> securityGroupKeys = groupDataList.stream()
                        .filter(group -> privateGroupKeys.contains(group.getGroup()) && group.getKey().contains("Security"))
                        .map(GroupData::getKey)
                        .collect(Collectors.toList());

                // 모든 관련 그룹 키를 하나의 리스트로 합침
                List<String> allRelevantGroupKeys = new ArrayList<>();
                allRelevantGroupKeys.addAll(vpcGroupKeys);
                allRelevantGroupKeys.addAll(availabilityGroupKeys);
                allRelevantGroupKeys.addAll(privateGroupKeys);
                allRelevantGroupKeys.addAll(securityGroupKeys);

                // 합쳐진 리스트에 속하는 그룹의 노드들만 고려하여 가장 아래쪽 노드의 위치 찾기
                for (NodeData node : nodeDataList) {
                        if (allRelevantGroupKeys.contains(node.getGroup())) {
                                try{
                                        String location = node.getLoc();
                                        if (location == null || location.isEmpty()) {
                                                throw new IllegalArgumentException("Location is null or empty for node: " + node);
                                        }

                                        String[] locParts = location.split(" ");
                                        if (locParts.length != 2) {
                                                throw new IllegalArgumentException("Location format is incorrect for node: " + node);
                                        }

                                        double x = Double.parseDouble(locParts[0]);
                                        double y = Double.parseDouble(locParts[1]);
                                        if (y > maxY || (y == maxY && x < maxX)) {
                                                maxY = y;
                                                maxX = x;
                                        }
                                }
                                catch (NumberFormatException e) {
                                } catch (IllegalArgumentException e) {
                                }
                        }
                }
                return new Point2D.Double(maxX, maxY+500);
        }

}

