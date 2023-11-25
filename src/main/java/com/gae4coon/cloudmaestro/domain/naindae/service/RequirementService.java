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

            for (ZoneDTO zone : Zones) {
                    if (zone.getZoneRequirements().contains("데이터베이스 분산")) {
                            int i = 0;
                            int regionIndex = 0; // 리전을 추적하기 위한 인덱스

                            Map<String, Object> result = new HashMap<>();

                            List<GroupData> regions = groupDataList.stream()
                                    .filter(group -> "AWS_Groups".equals(group.getType()) && group.getKey().contains("Region"))
                                    .collect(Collectors.toList());

                            // 각 리전별로 처리
                            for (GroupData region : regions) {
                                    // 2. 해당 리전의 'VPC' 그룹 식별
                                    Optional<GroupData> vpcGroup = groupDataList.stream()
                                            .filter(group -> group.getKey().contains("VPC") && group.getGroup() != null && group.getGroup().equals(region.getKey()))
                                            .findFirst();
                                    Point2D location = findLocForRegion(nodeDataList, groupDataList, region.getKey());

                                    if (vpcGroup.isPresent()) {
                                            String vpcKey = vpcGroup.get().getKey();
                                            Map<String, Object> rdsNodeCountInfo = countRdsNodesInPrivateSubnets(nodeDataList, groupDataList);
                                            int rdsNodeCount = (int) rdsNodeCountInfo.get("rdsNodeCount");
                                            List<String> rdsSubnetKeys = (List<String>) rdsNodeCountInfo.get("groupKeys");
                                            List<String> mrSubnetKeys = rdsSubnetKeys.stream()
                                                    .filter(key -> key.startsWith("MR-"))
                                                    .collect(Collectors.toList());
                                            List<String> nonMrSubnetKeys = rdsSubnetKeys.stream()
                                                    .filter(key -> !key.startsWith("MR-"))
                                                    .collect(Collectors.toList());
                                            // 3. AZ 생성 및 'VPC' 그룹에 추가
                                            String azKey = region.getKey()+"Multi-AZ"; // AZ 키 생성
                                            NodeData availabilityZoneNode = new NodeData();
                                            availabilityZoneNode.setKey(azKey);
                                            availabilityZoneNode.setText(azKey);
                                            availabilityZoneNode.setIsGroup(true);
                                            availabilityZoneNode.setGroup(vpcKey);
                                            availabilityZoneNode.setType("AWS_Groups");
                                            availabilityZoneNode.setStroke("rgb(0,164,166)");
                                            nodeDataList.add(availabilityZoneNode);

                                            GroupData newPrivateSubnet = new GroupData();
                                            newPrivateSubnet.setKey(azKey+"Private subnet");
                                            newPrivateSubnet.setText(azKey+"Private subnet");
                                            newPrivateSubnet.setIsGroup(true);
                                            newPrivateSubnet.setGroup(azKey);
                                            newPrivateSubnet.setType("AWS_Groups");
                                            newPrivateSubnet.setStroke("rgb(0,164,166)");
                                            groupDataList.add(newPrivateSubnet);
                                            if (regionIndex==0){
                                                    for (String subnetKey : nonMrSubnetKeys) {
                                                            String newLoc = location.getX()+(i*100) + " " + location.getY();

                                                            String rdsKey = availabilityZoneNode.getKey() + "RDS"; // RDS 키 생성
                                                            NodeData rdsNode = new NodeData();
                                                            rdsNode.setKey(rdsKey); // NAT 키를 고유하게 만듦
                                                            rdsNode.setText("RDS");
                                                            rdsNode.setLoc(newLoc); // 계산된 위치 설정
                                                            rdsNode.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                                                            rdsNode.setType("Arch_Database");
                                                            rdsNode.setGroup(newPrivateSubnet.getKey());
                                                            nodeDataList.add(rdsNode);

                                                            // RDS 노드와 서브넷을 연결하는 링크 생성
                                                            LinkData link = new LinkData();
                                                            link.setFrom(subnetKey); // 링크의 시작은 서브넷
                                                            link.setTo(newPrivateSubnet.getKey()); // 링크의 종료는 RDS 노드
                                                            linkDataList.add(link); // 링크 리스트에 추가
                                                            i++;
                                                    }
                                            }
                                            else{
                                                    for (String subnetKey : mrSubnetKeys) {
                                                            String newLoc = location.getX()+(i*100) + " " + location.getY();

                                                            String rdsKey = availabilityZoneNode.getKey() + "RDS"; // RDS 키 생성
                                                            NodeData rdsNode = new NodeData();
                                                            rdsNode.setKey(rdsKey); // NAT 키를 고유하게 만듦
                                                            rdsNode.setText("RDS");
                                                            rdsNode.setLoc(newLoc); // 계산된 위치 설정
                                                            rdsNode.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
                                                            rdsNode.setType("Arch_Database");
                                                            rdsNode.setGroup(newPrivateSubnet.getKey());
                                                            nodeDataList.add(rdsNode);

                                                            // RDS 노드와 서브넷을 연결하는 링크 생성
                                                            LinkData link = new LinkData();
                                                            link.setFrom(subnetKey); // 링크의 시작은 서브넷
                                                            link.setTo(newPrivateSubnet.getKey()); // 링크의 종료는 RDS 노드
                                                            // 필요한 추가 설정들...
                                                            linkDataList.add(link); // 링크 리스트에 추가
                                                            i++;
                                                    }
                                            }
                                            regionIndex++;
                                    }
                            }
                    }
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
                                String location = node.getLoc();
                                String[] locParts = location.split(" ");
                                double x = Double.parseDouble(locParts[0]);
                                double y = Double.parseDouble(locParts[1]);
                                if (y > maxY || (y == maxY && x < maxX)) {
                                        maxY = y;
                                        maxX = x;
                                }
                        }
                }
                return new Point2D.Double(maxX, maxY+500);
        }

}

