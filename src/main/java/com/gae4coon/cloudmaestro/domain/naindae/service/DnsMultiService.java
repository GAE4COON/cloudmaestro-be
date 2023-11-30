package com.gae4coon.cloudmaestro.domain.naindae.service;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import com.gae4coon.cloudmaestro.domain.ssohost.service.DiagramDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DnsMultiService {
    private final RegionService regionService; // final 키워드 추가
    private final DiagramDTOService diagramDTOService;

    public void getRequirementDns(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList,  List<LinkData> linkDataList, List<GroupData> groupDataList) {
        List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();

        // globalRequirements에 특정 문자열이 포함되어 있는지 확인
        if (globalRequirements.contains("DNS서비스 이중화 (Route53)") || globalRequirements.contains("리소스 이중화")) {
            boolean route53Exists = nodeDataList.stream()
                    .anyMatch(node -> node.getKey().contains("Route 53"));

            List<NodeData> newNodeDataList = new ArrayList<>();
            List<GroupData> newGroupDataList = new ArrayList<>();
            List<LinkData> newLinkDataList = new ArrayList<>();

            newNodeDataList = regionService.modifyNodeDataForNewRegion(nodeDataList);

            newGroupDataList = regionService.modifyGroupDataForNewRegion(groupDataList);

            newLinkDataList = regionService.modifyLinkDataForNewRegion(linkDataList);

            Point2D location = findRouteLoc(nodeDataList);

            String newLoc = (location.getX()-350) + " " + (location.getY()+300);
            String cdnLoc = (location.getX()-220) + " " + (location.getY()+300);

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

            newLinkDataList = addRouteLink(nodeDataList,newNodeDataList, newLinkDataList);
            newLinkDataList = addRouteToCloudFrontLink(nodeDataList,newLinkDataList);

            nodeDataList.addAll(newNodeDataList);
            groupDataList.addAll(newGroupDataList);
            linkDataList.addAll(newLinkDataList);
        } else {
            System.out.println("");
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

    public List<LinkData> addRouteLink(List<NodeData> nodeDataList, List<NodeData> newNodeDataList, List<LinkData> linkDataList) {

        List<NodeData> internetNodes = new ArrayList<>();
        for (NodeData node : nodeDataList) {
            if (node.getKey().contains("Internet")) {
                internetNodes.add(node);
            }
        }
        for (NodeData node : newNodeDataList) {
            if (node.getKey().contains("Internet")) {
                internetNodes.add(node);
            }
        }

        for (NodeData internetNode : internetNodes) {
            LinkData newLink = new LinkData();
            newLink.setFrom("Route 53");
            newLink.setTo(internetNode.getKey());
            linkDataList.add(newLink);
        }

            return linkDataList;
    }
    public List<LinkData> addRouteToCloudFrontLink(List<NodeData> nodeDataList, List<LinkData> linkDataList) {
        NodeData routeNode = null, cloudFrontNode = null;

        for (NodeData node : nodeDataList) {
            if (node.getKey().equals("Route 53")) {
                routeNode = node;
            } else if (node.getKey().equals("CloudFront")) {
                cloudFrontNode = node;
            }
        }

        if (routeNode != null && cloudFrontNode != null) {
            LinkData link = new LinkData();
            link.setFrom(routeNode.getKey());
            link.setTo(cloudFrontNode.getKey());
            linkDataList.add(link);
        }

        return linkDataList;
    }


}
