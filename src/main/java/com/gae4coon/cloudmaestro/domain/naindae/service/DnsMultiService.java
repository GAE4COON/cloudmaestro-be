package com.gae4coon.cloudmaestro.domain.naindae.service;
import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DnsMultiService {
    private final RegionService regionService; // final 키워드 추가

    public Map<String, Object> getRequirementDns(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData> linkDataList) {
        Map<String, Object> result = new HashMap<>();

        List<NodeData> newNodeDataList = new ArrayList<>();
        List<GroupData> newGroupDataList = new ArrayList<>();
        List<LinkData> newLinkDataList = new ArrayList<>();

        newNodeDataList = regionService.modifyNodeDataForNewRegion(nodeDataList);
        newGroupDataList = regionService.modifyGroupDataForNewRegion(groupDataList);
        newLinkDataList = regionService.modifyLinkDataForNewRegion(linkDataList);

        Point2D location = findRouteLoc(nodeDataList);

        String newLoc = (location.getX()-350) + " " + (location.getY()+300);

        NodeData routeNode = new NodeData();
        routeNode.setKey("Route 53"); // NAT 키를 고유하게 만듦
        routeNode.setText("Route 53");
        routeNode.setLoc(newLoc); // 계산된 위치 설정
        routeNode.setSource("/img/AWS_icon/Arch_Networking-Content-Delivery/Arch_Amazon-Route-53_48.svg");
        routeNode.setType("Networking-Content-Delivery");
        nodeDataList.add(routeNode);


        newLinkDataList = addRouteLink(nodeDataList,newNodeDataList, newLinkDataList);

        result.put("nodes", newNodeDataList);
        result.put("groups", newGroupDataList);
        result.put("links", newLinkDataList);

        return result;
    }
    public Point2D findRouteLoc(List<NodeData> nodeDataList) {
        double minX = Double.MAX_VALUE;
        double minY = 0;
        for (NodeData node : nodeDataList) {
            String location = node.getLoc();
            String[] locParts = location.split(" ");
            double x = Double.parseDouble(locParts[0]);
            double y = Double.parseDouble(locParts[1]);

            if (x < minX) {
                minX = x;
                minY = y;
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
        System.out.println("internet nodes: "+internetNodes);

        for (NodeData internetNode : internetNodes) {
            LinkData newLink = new LinkData();
            newLink.setFrom("Route 53");
            newLink.setTo(internetNode.getKey());
            linkDataList.add(newLink);
        }

            return linkDataList;
    }

}
