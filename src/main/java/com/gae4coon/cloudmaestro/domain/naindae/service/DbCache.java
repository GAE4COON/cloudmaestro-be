package com.gae4coon.cloudmaestro.domain.naindae.service;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbCache {
    public void createNode(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList){
        List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();
        if((globalRequirements.contains("부하 분산") || globalRequirements.contains("RDS 읽기복제 및 캐싱 (RDS)"))){

            boolean route53Exists = nodeDataList.stream()
                    .anyMatch(node -> node.getKey().contains("Route 53"));

            List<GroupData> regions = groupDataList.stream()
                    .filter(group -> "AWS_Groups".equals(group.getType()) && group.getKey().contains("Region"))
                    .collect(Collectors.toList());

            System.out.println("regions data:"+regions);
            Point2D location = findRouteLoc(nodeDataList);
            String newLoc = (location.getX()-350) + " " + (location.getY()+300);
            String cdnLoc = (location.getX()-350) + " " + (location.getY()+150);

            if (!route53Exists) {
                NodeData routeNode = new NodeData();
                routeNode.setKey("Route 53"); // NAT 키를 고유하게 만듦
                routeNode.setText("Route 53");
                routeNode.setLoc(newLoc); // 계산된 위치 설정
                routeNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-Route-53_48.svg");
                routeNode.setType("Networking-Content-Delivery");
                nodeDataList.add(routeNode);
            }

            NodeData cdnNode = new NodeData();
            cdnNode.setKey("CloudFront"); // NAT 키를 고유하게 만듦
            cdnNode.setText("CloudFront");
            cdnNode.setLoc(cdnLoc); // route53에서 x값을 오른쪽으로 설정해줘야함
            cdnNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-CloudFront_48.svg");
            cdnNode.setType("Networking-Content-Delivery");
            nodeDataList.add(cdnNode);

            for(GroupData region: regions) {
                NodeData s3Node = new NodeData();
                s3Node.setKey("S3"); // NAT 키를 고유하게 만듦
                s3Node.setText("S3");
                s3Node.setLoc(region.getLoc()); // route53에서 x값을 오른쪽으로 설정해줘야함 여기
                s3Node.setSource("/img/AWS_icon/Arch_Storage/Res_Amazon-Simple-Storage-Service_S3-Standard_48.svg");
                s3Node.setType("Storage");
                nodeDataList.add(s3Node);
            }
        }
    }
    public Point2D findRouteLoc(List<NodeData> nodeDataList) {
        double minX = Double.MAX_VALUE;
        double minY = 0;
        for (NodeData node : nodeDataList) {
            try {
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

                if (x < minX) {
                    minX = x;
                    minY = y;
                }
            }
            catch (NumberFormatException e) {
            } catch (IllegalArgumentException e) {
            }
        }
        return new Point2D.Double(minX, minY);
    }
}
