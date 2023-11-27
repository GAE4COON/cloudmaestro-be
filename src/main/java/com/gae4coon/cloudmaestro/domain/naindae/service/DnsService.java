package com.gae4coon.cloudmaestro.domain.naindae.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.GroupData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.LinkData;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.NodeData;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.List;

@Service
public class DnsService {
    public void createDns(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList) {
        List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();
        boolean route53Exists = nodeDataList.stream()
                .anyMatch(node -> node.getKey().contains("Route 53"));

        if ((globalRequirements.contains("부하 분산") || globalRequirements.contains("DNS 서비스")) && !route53Exists ){
            Point2D location = findRouteLoc(nodeDataList);
            long regionCount = groupDataList.stream()
                    .filter(group -> group.getKey().contains("Region"))
                    .count();
            String newLoc;
            if (regionCount>=2){
                newLoc = (location.getX()-350) + " " + (location.getY()+300);

            }
            else {
                newLoc = (location.getX()-350) + " " + (location.getY()+100);
            }
            NodeData routeNode = new NodeData();
            routeNode.setKey("Route 53"); // NAT 키를 고유하게 만듦
            routeNode.setText("Route 53");
            routeNode.setLoc(newLoc); // 계산된 위치 설정
            routeNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-Route-53_48.svg");
            routeNode.setType("Networking-Content-Delivery");
            nodeDataList.add(routeNode);

            for (NodeData node : nodeDataList) {
                if (node.getKey().contains("Internet")) {
                    LinkData newLink = new LinkData();
                    newLink.setFrom("Route 53"); // 새로 생성된 'Route 53' 노드
                    newLink.setTo(node.getKey()); // 찾은 'Internet Gateway' 노드
                    linkDataList.add(newLink);
                }
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
