package com.gae4coon.cloudmaestro.domain.alert.service;

import com.gae4coon.cloudmaestro.domain.ssohost.dto.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DbAccessGuideAlertService {
    public String dbCheck(List<NodeData> nodeDataList, List<GroupData> groupDataList, List<LinkData>linkDataList){
        Set<String> databaseGroups = new HashSet<>();
        Set<String> databaseNodes = new HashSet<>();
        Set<String> ec2Groups = new HashSet<>();

        String result = "false";
        for (NodeData node : nodeDataList) {
            if ("Database".equals(node.getType())) {
                databaseGroups.add(node.getGroup());
                databaseNodes.add(node.getKey());
            }
            if ("EC2".contains(node.getText())) {
                ec2Groups.add(node.getGroup());
            }
        }

        for (LinkData link : linkDataList) {
            if (databaseGroups.contains(link.getTo()) && !ec2Groups.contains(link.getFrom()) && !link.getFrom().contains("EC2")) {
                result = "true";
                break;
            }
        }


        for (LinkData link : linkDataList) {
            if (databaseNodes.contains(link.getTo()) && !ec2Groups.contains(link.getFrom()) && !link.getFrom().contains("EC2")) {
                result = "true";
                break;
            }
        }

        return result;
    }
}
