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
public class DbCache {
    public void createNode(RequireDiagramDTO requireDiagramDTO, List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList){
        List<String> globalRequirements = requireDiagramDTO.getRequirementData().getGlobalRequirements();
        if((globalRequirements.contains("부하 분산") || globalRequirements.contains("RDS 읽기복제 및 캐싱"))) {
            List<String> rdsGroups = new ArrayList<>();

            for (NodeData node : nodeDataList) {
                if (node.getKey().contains("RDS")) {
                    if ( node.getGroup() != null && !rdsGroups.contains( node.getGroup())) {
                        rdsGroups.add( node.getGroup());
                    }
                }
            }
            addNode(nodeDataList, linkDataList, groupDataList, rdsGroups);
        }
    }
    public void addNode(List<NodeData> nodeDataList, List<LinkData> linkDataList, List<GroupData> groupDataList, List<String> rdsGroups){
        int num=1;
        for(String rdsGroup: rdsGroups){
            Point2D rightMostNodeLocation = findRightMostNodeLocation(rdsGroup, nodeDataList);
            String newLoc;
            newLoc = (rightMostNodeLocation.getX()+100) + " " + (rightMostNodeLocation.getY());
            NodeData rdsReplicaNode = new NodeData();
            rdsReplicaNode.setKey("RDS Read Replica"+num); // NAT 키를 고유하게 만듦
            rdsReplicaNode.setText("RDS Read Replica"+num);
            rdsReplicaNode.setLoc(newLoc); // route53에서 x값을 오른쪽으로 설정해줘야함
            rdsReplicaNode.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-RDS_48.svg");
            rdsReplicaNode.setType("Arch_Database");
            rdsReplicaNode.setGroup(rdsGroup);
            nodeDataList.add(rdsReplicaNode);

            NodeData cacheNode = new NodeData();
            newLoc = (rightMostNodeLocation.getX()+200) + " " + (rightMostNodeLocation.getY());
            cacheNode.setKey("ElastiCache"+num); // NAT 키를 고유하게 만듦
            cacheNode.setText("ElastiCache"+num);
            cacheNode.setLoc(newLoc); // route53에서 x값을 오른쪽으로 설정해줘야함
            cacheNode.setSource("/img/AWS_icon/Arch_Database/Arch_Amazon-ElastiCache_48.svg");
            cacheNode.setType("Database");
            cacheNode.setGroup(rdsGroup);
            nodeDataList.add(cacheNode);
            num++;
        }
    }
    private Point2D findRightMostNodeLocation(String groupName, List<NodeData> nodeDataList) {
        double maxX = Double.MIN_VALUE;
        Point2D rightMostNodeLocation = null;

        for (NodeData node : nodeDataList) {
            if (groupName.equals(node.getGroup())) {
                String location = node.getLoc();
                if (location != null && !location.isEmpty()) {
                    String[] locParts = location.split(" ");
                    double x = Double.parseDouble(locParts[0]);
                    double y = Double.parseDouble(locParts[1]);

                    if (x > maxX) {
                        maxX = x;
                        rightMostNodeLocation = new Point2D.Double(x, y);
                    }
                }
            }
        }
        return rightMostNodeLocation;
    }
}
