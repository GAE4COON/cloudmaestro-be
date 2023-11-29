package com.gae4coon.cloudmaestro.domain.naindae.service;

import com.gae4coon.cloudmaestro.domain.requirements.dto.RequireDiagramDTO;
import com.gae4coon.cloudmaestro.domain.requirements.dto.ZoneDTO;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudFrontDistribution {
    public void createNode(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList) {
        List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();
        if((globalRequirements.contains("부하 분산") || globalRequirements.contains("정적,동적컨텐츠 분산 (CloudFront)"))){
            addNode(nodeDataList,linkDataList,groupDataList);
            addLink(nodeDataList,linkDataList,groupDataList);
        }
    }

    public void addNode(List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList){
        boolean route53Exists = nodeDataList.stream()
                .anyMatch(node -> node.getKey().contains("Route 53"));
        boolean cdnExists = nodeDataList.stream()
                .anyMatch(node -> node.getKey().contains("CloudFront"));
        boolean s3Exists = nodeDataList.stream()
                .anyMatch(node -> node.getKey().equals("Simple Storage Service (S3)"));

        List<GroupData> regions = groupDataList.stream()
                .filter(group -> "AWS_Groups".equals(group.getType()) && group.getKey().contains("Region") && group.getGroup()==null)
                .collect(Collectors.toList());

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

        if(!cdnExists) {
            NodeData cdnNode = new NodeData();
            cdnNode.setKey("CloudFront"); // NAT 키를 고유하게 만듦
            cdnNode.setText("CloudFront");
            cdnNode.setLoc(cdnLoc); // route53에서 x값을 오른쪽으로 설정해줘야함
            cdnNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-CloudFront_48.svg");
            cdnNode.setType("Networking-Content-Delivery");
            nodeDataList.add(cdnNode);
        }

        if (!s3Exists) {
            int num=1;
            for (GroupData region : regions) {
                System.out.println("Region loc:"+region.getLoc());
                NodeData s3Node = new NodeData();
                s3Node.setKey("Simple Storage Service (S3)"+num); // NAT 키를 고유하게 만듦
                s3Node.setText("Simple Storage Service (S3)"+num);
                s3Node.setLoc(region.getLoc()); // route53에서 x값을 오른쪽으로 설정해줘야함 여기
                s3Node.setSource("/img/AWS_icon/Arch_Storage/Arch_Amazon-Simple-Storage-Service_48.svg");
                s3Node.setType("Storage");
                nodeDataList.add(s3Node);
                num++;
            }
        }
    }

    public void addLink(List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList){
        //todo 여기서 link로
        String routeNode = "";
        String cdnNode = "";
        List<String> s3Nodes = new ArrayList<>();
        List<String> internetGatewayNodes = new ArrayList<>();

        for (NodeData node : nodeDataList) {
            if (node.getKey().contains("Route 53") && routeNode.isEmpty()) {
                routeNode = node.getKey();
            } else if (node.getKey().contains("Internet Gateway")) {
                internetGatewayNodes.add(node.getKey());
            } else if (node.getKey().contains("CloudFront") && cdnNode.isEmpty()) {
                cdnNode = node.getKey();
            } else if (node.getKey().contains("Simple Storage Service (S3)")) {
                s3Nodes.add(node.getKey());
            }
        }

        for (String internetGatewayNode : internetGatewayNodes) {
            LinkData link = new LinkData();
            link.setFrom(routeNode);
            link.setTo(internetGatewayNode);
            linkDataList.add(link);

            LinkData link4 = new LinkData();
            link4.setFrom(cdnNode);
            link4.setTo(internetGatewayNode);
            linkDataList.add(link4);
        }

        LinkData link2 = new LinkData();
        link2.setFrom(routeNode);
        link2.setTo(cdnNode);
        linkDataList.add(link2);


        for (String s3Node : s3Nodes) {
            LinkData link3 = new LinkData();
            link3.setFrom(cdnNode);
            link3.setTo(s3Node);
            linkDataList.add(link3);
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
